package com.example.simplenote.data

import com.example.simplenote.data.remote.AuthApi
import com.example.simplenote.data.remote.NetworkResult
import com.example.simplenote.data.remote.TokenObtainPairRequest
import com.example.simplenote.data.remote.TokenObtainPairResponse
import com.example.simplenote.data.remote.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) {
    fun login(username: String, password: String): Flow<NetworkResult<TokenObtainPairResponse>> = flow {
        try {
            val response = api.obtainToken(TokenObtainPairRequest(username, password))
            tokenStore.setTokens(response.access, response.refresh)
            emit(NetworkResult.Success(response))
        } catch (e: HttpException) {
            val code = e.code()
            val errorBody = e.response()?.errorBody()?.string()
            val msg = parseValidationError(errorBody) ?: e.message()
            emit(NetworkResult.HttpError(code, msg))
        } catch (e: IOException) {
            emit(NetworkResult.NetworkError(e))
        } catch (e: Exception) {
            emit(NetworkResult.UnknownError(e))
        }
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String
    ): NetworkResult<Unit> {
        return try {
            val response = api.register(
                RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    email = email,
                    password = password
                )
            )
            tokenStore.setTokens(response.access, response.refresh)
            NetworkResult.Success(Unit)
        } catch (e: HttpException) {
            val code = e.code()
            val errorBody = e.response()?.errorBody()?.string()
            val msg = parseValidationError(errorBody) ?: e.message()
            NetworkResult.HttpError(code, msg)
        } catch (e: IOException) {
            NetworkResult.NetworkError(e)
        } catch (e: Exception) {
            NetworkResult.UnknownError(e)
        }
    }

    private fun parseValidationError(errorBody: String?): String? {
        if (errorBody == null) return null
        return try {
            val json = JSONObject(errorBody)
            if (json.optString("type") == "validation_error") {
                val errors = json.optJSONArray("errors") ?: return null
                val details = mutableListOf<String>()
                for (i in 0 until errors.length()) {
                    val err = errors.optJSONObject(i)
                    val detail = err?.optString("detail")
                    if (!detail.isNullOrBlank()) details.add(detail)
                }
                if (details.isNotEmpty()) details.joinToString("\n") else null
            } else null
        } catch (ex: Exception) {
            null
        }
    }
}
