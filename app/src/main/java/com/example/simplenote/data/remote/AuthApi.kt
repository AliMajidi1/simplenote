package com.example.simplenote.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class TokenObtainPairRequest(val username: String, val password: String)

@Serializable
data class TokenObtainPairResponse(
    val access: String,
    val refresh: String
)

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponse(
    val access: String,
    val refresh: String
)

interface AuthApi {
    @POST("/api/auth/token/")
    suspend fun obtainToken(@Body body: TokenObtainPairRequest): TokenObtainPairResponse

    @POST("/api/auth/register/")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse
}

sealed interface NetworkResult<out T> {
    data class Success<T>(val value: T): NetworkResult<T>
    data class HttpError(val code: Int, val message: String?): NetworkResult<Nothing>
    data class NetworkError(val throwable: Throwable): NetworkResult<Nothing>
    data class UnknownError(val throwable: Throwable): NetworkResult<Nothing>
}
