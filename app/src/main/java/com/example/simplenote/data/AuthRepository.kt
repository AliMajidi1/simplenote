package com.example.simplenote.data

import com.example.simplenote.data.remote.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication-related operations.
 * Handles login, registration, and token management.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) {
    /**
     * Attempts to log in with the given credentials.
     * @param username The user's username.
     * @param password The user's password.
     * @return A Flow emitting the result of the login operation.
     */
    fun login(username: String, password: String): Flow<NetworkResult<TokenObtainPairResponse>> = flow {
        try {
            val response = api.obtainToken(TokenObtainPairRequest(username, password))
            tokenStore.setTokens(response.access, response.refresh)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(handleException(e))
        }
    }

    /**
     * Registers a new user with the provided information.
     * @return NetworkResult indicating success or error.
     */
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
        } catch (e: Exception) {
            handleException(e)
        }
    }

    /**
     * Fetches the current user's profile info.
     */
    fun getUserInfo(): Flow<NetworkResult<UserInfoResponse>> = flow {
        try {
            val response = api.getUserInfo()
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(handleException(e))
        }
    }

    /**
     * Changes the user's password.
     * Returns NetworkResult for consistent error handling.
     */
    suspend fun changePassword(oldPassword: String, newPassword: String): NetworkResult<Unit> {
        return try {
            api.changePassword(
                ChangePasswordRequest(
                    old_password = oldPassword,
                    new_password = newPassword
                )
            )
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    /**
     * Logs out the user by clearing tokens.
     */
    suspend fun logout() {
        tokenStore.clearTokens()
    }

    /**
     * Handles exceptions and maps them to NetworkResult.
     */
    private fun <T> handleException(e: Exception): NetworkResult<T> {
        return when (e) {
            is HttpException -> {
                val code = e.code()
                val errorBody = e.response()?.errorBody()?.string()
                val msg = parseValidationError(errorBody) ?: e.message()
                NetworkResult.HttpError(code, msg)
            }
            is IOException -> NetworkResult.NetworkError(e)
            else -> NetworkResult.UnknownError(e)
        }
    }

    /**
     * Parses validation errors from the API error body.
     */
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