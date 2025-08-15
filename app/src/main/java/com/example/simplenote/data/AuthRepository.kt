package com.example.simplenote.data

import com.example.simplenote.data.remote.AuthApi
import com.example.simplenote.data.remote.NetworkResult
import com.example.simplenote.data.remote.TokenObtainPairRequest
import com.example.simplenote.data.remote.TokenObtainPairResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
            val msg = e.response()?.errorBody()?.string() ?: e.message()
            emit(NetworkResult.HttpError(code, msg))
        } catch (e: IOException) {
            emit(NetworkResult.NetworkError(e))
        } catch (e: Exception) {
            emit(NetworkResult.UnknownError(e))
        }
    }
}
