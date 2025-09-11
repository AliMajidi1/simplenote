package com.example.simplenote.di

import android.content.Context
import com.example.simplenote.BuildConfig
import com.example.simplenote.data.AuthRepository
import com.example.simplenote.data.TokenAuthenticator
import com.example.simplenote.data.TokenStore
import com.example.simplenote.data.local.AppDatabase
import com.example.simplenote.data.remote.AuthApi
import com.example.simplenote.data.remote.NotesApi
import io.sentry.okhttp.SentryOkHttpInterceptor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import androidx.room.Room

class SessionManager {
    private val _sessionExpired = MutableSharedFlow<Unit>(replay = 1)
    val sessionExpired = _sessionExpired.asSharedFlow()
    suspend fun notifySessionExpired() { _sessionExpired.emit(Unit) }
}

val NetworkModule = module {
    single { SessionManager() }
    single { TokenStore(get<Context>()) }
    single { provideOkHttpClient(get(), { get<AuthApi>() }, get()) }
    single { provideRetrofit(get(), getProperty("BASE_URL")) }
    single { get<Retrofit>().create(AuthApi::class.java) }
    single { AuthRepository(get(), get()) }
    single { get<Retrofit>().create(NotesApi::class.java) }
    single {
        Room.databaseBuilder(get<Context>(), AppDatabase::class.java, "notes_db")
            .fallbackToDestructiveMigration(true)
            .build()
    }
    single { get<AppDatabase>().noteDao() }
    single { com.example.simplenote.data.NotesRepository(get(), get()) }
    viewModel { com.example.simplenote.ui.screens.LoginViewModel(get()) }
    viewModel { com.example.simplenote.ui.screens.HomeViewModel(get()) }
    viewModel { com.example.simplenote.ui.screens.NoteEditViewModel(get()) }
    viewModel { com.example.simplenote.ui.screens.SettingsViewModel(get()) }
    viewModel { com.example.simplenote.ui.screens.ChangePasswordViewModel(get()) }
}

@OptIn(DelicateCoroutinesApi::class)
private fun provideOkHttpClient(
    tokenStore: TokenStore,
    authApiProvider: () -> AuthApi,
    sessionManager: SessionManager
): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(SentryOkHttpInterceptor())
        .addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val isAuthEndpoint = original.url.encodedPath.startsWith("/api/auth/")
            if (isAuthEndpoint) {
                return@Interceptor chain.proceed(original)
            }
            val tokens = runBlocking { tokenStore.tokensFlow.first() }
            val accessToken = tokens?.accessToken
            if (!accessToken.isNullOrBlank()) {
                val newRequest = original.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(original)
            }
        })
        .authenticator(TokenAuthenticator(tokenStore, authApiProvider) {
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
