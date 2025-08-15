package com.example.simplenote.di

import android.content.Context
import com.example.simplenote.BuildConfig
import com.example.simplenote.data.AuthRepository
import com.example.simplenote.data.TokenStore
import com.example.simplenote.data.remote.AuthApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val NetworkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get(), getProperty("BASE_URL")) }
    single { get<Retrofit>().create(AuthApi::class.java) }
    single { TokenStore(get<Context>()) }
    single { AuthRepository(get(), get()) }
    viewModel { com.example.simplenote.ui.screens.LoginViewModel(get()) }
}

private fun provideOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
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
