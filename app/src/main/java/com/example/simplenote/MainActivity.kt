package com.example.simplenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.simplenote.data.TokenStore
import com.example.simplenote.nav.AppNavHost
import com.example.simplenote.ui.theme.SimplenoteTheme
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val tokenStore = get<TokenStore>()
        setContent {
            SimplenoteTheme {
                AppNavHost(tokenStore = tokenStore)
            }
        }
    }
}
