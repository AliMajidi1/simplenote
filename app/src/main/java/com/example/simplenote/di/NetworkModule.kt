package com.example.simplenote.di

import android.content.Context
import com.example.simplenote.BuildConfig
import com.example.simplenote.data.AuthRepository
import com.example.simplenote.data.TokenAuthenticator
import com.example.simplenote.data.TokenStore
import com.example.simplenote.data.remote.AuthApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SessionManager {
    private val _sessionExpired = MutableSharedFlow<Unit>(replay = 1)
    val sessionExpired = _sessionExpired.asSharedFlow()
    suspend fun notifySessionExpired() { _sessionExpired.emit(Unit) }
}

val NetworkModule = module {
    single { SessionManager() }
    single { provideOkHttpClient(get(), get(), get()) }
    single { provideRetrofit(get(), getProperty("BASE_URL")) }
    single { get<Retrofit>().create(AuthApi::class.java) }
    single { TokenStore(get<Context>()) }
    single { AuthRepository(get(), get()) }
    viewModel { com.example.simplenote.ui.screens.LoginViewModel(get()) }
}

private fun provideOkHttpClient(
    tokenStore: TokenStore,
    authApi: AuthApi,
    sessionManager: SessionManager
): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .authenticator(TokenAuthenticator(tokenStore, authApi) {
            GlobalScope.launch {
                sessionManager.notifySessionExpired()
            }
        })
    if (BuildConfig.DEBUG) {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        builder.addInterceptor(logging)
    }
    return builder.build()
}

private fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}
