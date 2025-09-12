package com.example.simplenote.data.remote

import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
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
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponse(
    val access: String,
    val refresh: String
)

@Serializable
data class TokenRefreshRequest(val refresh: String)

@Serializable
data class TokenRefreshResponse(val access: String)

@Serializable
data class UserInfoResponse(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String
)

@Serializable
data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String
)

@Serializable
data class MessageResponse(val detail: String)

interface AuthApi {
    @POST("/api/auth/token/")
    suspend fun obtainToken(@Body body: TokenObtainPairRequest): TokenObtainPairResponse

    @POST("/api/auth/register/")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("/api/auth/token/refresh/")
    suspend fun refreshToken(@Body body: TokenRefreshRequest): TokenRefreshResponse

    @GET("/api/auth/userinfo/")
    suspend fun getUserInfo(): UserInfoResponse

    @POST("/api/auth/change-password/")
    suspend fun changePassword(@Body body: ChangePasswordRequest): MessageResponse
}

sealed interface NetworkResult<out T> {
    data class Success<T>(val value: T): NetworkResult<T>
    data class HttpError(val code: Int, val message: String?): NetworkResult<Nothing>
    data class NetworkError(val throwable: Throwable): NetworkResult<Nothing>
    data class UnknownError(val throwable: Throwable): NetworkResult<Nothing>
}