package com.example.simplenote.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplenote.ui.screens.OnboardingScreen
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.simplenote.ui.screens.LoginScreen
import com.example.simplenote.ui.screens.LoginViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.simplenote.data.TokenStore
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.State
import com.example.simplenote.ui.screens.RegisterScreen

object Destinations {
    const val Onboarding = "onboarding"
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    tokenStore: TokenStore
) {
    val tokensState = tokenStore.tokensFlow.collectAsState(initial = null)
    val tokens = tokensState.value
    var startDestination = Destinations.Onboarding
    if (tokens != null) {
        startDestination = Destinations.Home
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destinations.Onboarding) {
            OnboardingScreen(
                onGetStarted = { navController.navigate(Destinations.Login) }
            )
        }
        composable(Destinations.Login) {
            val viewModel = koinViewModel<LoginViewModel>()
            LoginScreen(
                viewModel = viewModel,
                onNavigateHome = {
                    navController.navigate(Destinations.Home) {
                        popUpTo(Destinations.Login) { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(Destinations.Register)
                }
            )
        }
        composable(Destinations.Register) {
            RegisterScreen(
                onBackToLogin = { navController.navigateUp() },
                onRegisterSuccess = { navController.navigate(Destinations.Login) }
            )
        }
        composable(Destinations.Home) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Home Screen (Stub)")
            }
        }
    }
}
