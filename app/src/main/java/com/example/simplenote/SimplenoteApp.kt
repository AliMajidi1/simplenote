package com.example.simplenote

import android.app.Application
import com.example.simplenote.di.NetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import io.sentry.android.core.SentryAndroid

class SimplenoteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            SentryAndroid.init(this) { options ->
                options.tracesSampleRate = 1.0
                options.isDebug = true
            }
        } catch (_: Exception) {
        }

        startKoin {
            androidContext(this@SimplenoteApp)
            modules(NetworkModule)
            properties(mapOf("BASE_URL" to "https://simple.darkube.app/api/"))
//            properties(mapOf("BASE_URL" to "http://127.0.0.1:8000/api/"))
        }
    }
}
