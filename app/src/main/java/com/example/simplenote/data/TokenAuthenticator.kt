package com.example.simplenote.data

import android.util.Log
import com.example.simplenote.data.remote.AuthApi
import com.example.simplenote.data.remote.TokenRefreshRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.atomic.AtomicBoolean

/**
 * OkHttp Authenticator that refreshes JWT access tokens using the refresh token.
 * Notifies on session expiration via a callback.
 */
class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val authApiProvider: () -> AuthApi,
    private val onSessionExpired: () -> Unit
) : Authenticator {
    @Volatile private var isRefreshing = AtomicBoolean(false)

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        return runBlocking(Dispatchers.IO) {
            val tokens = tokenStore.tokensFlow.first() ?: return@runBlocking null
            val refreshToken = tokens.refreshToken
            if (refreshToken.isBlank()) return@runBlocking null

            if (!isRefreshing.compareAndSet(false, true)) {
                while (isRefreshing.get()) {
                    kotlinx.coroutines.delay(50)
                }
                val newTokens = tokenStore.tokensFlow.first() ?: return@runBlocking null
                val newAccess = newTokens.accessToken
                if (newAccess.isBlank()) return@runBlocking null
                return@runBlocking response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccess")
                    .build()
            }
            try {
                val refreshResponse = authApiProvider().refreshToken(TokenRefreshRequest(refresh = refreshToken))
                tokenStore.setTokens(refreshResponse.access, refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshResponse.access}")
                    .build()
            } catch (e: Exception) {
                Log.e("TokenAuthenticator", "Token refresh failed", e)
                tokenStore.clearTokens()
                onSessionExpired()
                null
            } finally {
                isRefreshing.set(false)
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
