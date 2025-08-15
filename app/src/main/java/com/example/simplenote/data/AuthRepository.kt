package com.example.simplenote.data

import com.example.simplenote.data.remote.AuthApi
import com.example.simplenote.data.remote.LoginRequest
import com.example.simplenote.data.remote.NetworkResult
import com.example.simplenote.data.remote.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val api: AuthApi,
    private val tokenStore: TokenStore
) {
    fun login(email: String, password: String): Flow<NetworkResult<UserDto>> = flow {
        try {
            val response = api.login(LoginRequest(email, password))
            tokenStore.setTokens(response.accessToken, response.refreshToken)
            emit(NetworkResult.Success(response.user))
        } catch (e: HttpException) {
            val code = e.code()
            val msg = e.response()?.errorBody()?.string() ?: e.message()
            emit(NetworkResult.HttpError(code, msg))
        } catch (e: IOException) {
            emit(NetworkResult.NetworkError(e))
        } catch (e: Exception) {
            emit(NetworkResult.UnknownError(e))
        }
    }
}

