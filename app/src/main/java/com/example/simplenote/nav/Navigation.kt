package com.example.simplenote.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplenote.data.TokenStore
import com.example.simplenote.ui.screens.LoginScreen
import com.example.simplenote.ui.screens.LoginViewModel
import com.example.simplenote.ui.screens.OnboardingScreen
import com.example.simplenote.ui.screens.RegisterScreen
import org.koin.androidx.compose.koinViewModel

object Destinations {
    const val Onboarding = "onboarding"
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    tokenStore: TokenStore,
    shouldNavigateToLogin: MutableState<Boolean>
) {
    val tokensState = tokenStore.tokensFlow.collectAsState(initial = null)
    val tokens = tokensState.value
    var startDestination = Destinations.Onboarding
    if (tokens != null) {
        startDestination = Destinations.Home
    }
    LaunchedEffect(shouldNavigateToLogin.value) {
        if (shouldNavigateToLogin.value) {
            navController.navigate(Destinations.Login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            shouldNavigateToLogin.value = false
        }
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
            com.example.simplenote.ui.screens.HomeScreen(
                onAddNote = { /* TODO: Implement add note navigation */ },
                onNoteClick = { /* TODO: Implement note detail navigation */ },
                onSettingsClick = { navController.navigate("settings") },
            )
        }
    }
}
