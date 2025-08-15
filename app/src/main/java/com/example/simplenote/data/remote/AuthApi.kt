package com.example.simplenote.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String
)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse
}

sealed interface NetworkResult<out T> {
    data class Success<T>(val value: T): NetworkResult<T>
    data class HttpError(val code: Int, val message: String?): NetworkResult<Nothing>
    data class NetworkError(val throwable: Throwable): NetworkResult<Nothing>
    data class UnknownError(val throwable: Throwable): NetworkResult<Nothing>
}

