package com.example.simplenote

import android.app.Application
import com.example.simplenote.di.NetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SimplenoteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SimplenoteApp)
            modules(NetworkModule)
        }
    }
}

