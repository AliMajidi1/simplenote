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

object Destinations {
    const val Onboarding = "onboarding"
    const val Home = "home"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.Onboarding
    ) {
        composable(Destinations.Onboarding) {
            OnboardingScreen(
                onGetStarted = { navController.navigate(Destinations.Home) }
            )
        }
        composable(Destinations.Home) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Home Screen")
            }
        }
    }
}
