package com.example.simplenote

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.simplenote.data.TokenStore
import com.example.simplenote.di.SessionManager
import com.example.simplenote.nav.AppNavHost
import com.example.simplenote.ui.theme.SimplenoteTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val tokenStore = get<TokenStore>()
        val sessionManager = get<SessionManager>()
        setContent {
            SimplenoteTheme {
                AppNavHost(tokenStore = tokenStore)
            }
        }
        lifecycleScope.launch {
            sessionManager.sessionExpired.collectLatest { _: Unit ->
                Toast.makeText(this@MainActivity, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
                // TODO: Implement navigation to Login screen (e.g., via NavController or state)
            }
        }
    }
}
