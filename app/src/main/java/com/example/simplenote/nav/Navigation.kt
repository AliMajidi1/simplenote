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
import com.example.simplenote.ui.screens.*
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

object Destinations {
    const val Onboarding = "onboarding"
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val NoteEdit = "note_edit"
    const val Settings = "settings"
    const val ChangePassword = "change_password"
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
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val shouldReloadNotes = currentBackStackEntry?.savedStateHandle?.get<Boolean>("shouldReloadNotes") == true
            com.example.simplenote.ui.screens.HomeScreen(
                onAddNote = { navController.navigate(Destinations.NoteEdit) },
                onNoteClick = { note -> navController.navigate("${Destinations.NoteEdit}/${note.id}") },
                onSettingsClick = { navController.navigate(Destinations.Settings) }, // Use constant
                shouldReloadNotes = shouldReloadNotes,
                onReloadConsumed = {
                    currentBackStackEntry?.savedStateHandle?.set("shouldReloadNotes", false)
                }
            )
        }
        composable(Destinations.NoteEdit) {
            NoteEditScreen(
                noteId = null,
                onBack = { navController.navigateUp() },
                onNoteDeleted = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("shouldReloadNotes", true)
                    navController.popBackStack(Destinations.Home, false)
                },
                onNoteSaved = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("shouldReloadNotes", true)
                    navController.popBackStack(Destinations.Home, false)
                }
            )
        }
        composable("${Destinations.NoteEdit}/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            NoteEditScreen(
                noteId = noteId,
                onBack = { navController.navigateUp() },
                onNoteDeleted = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("shouldReloadNotes", true)
                    navController.popBackStack(Destinations.Home, false)
                },
                onNoteSaved = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("shouldReloadNotes", true)
                    navController.popBackStack(Destinations.Home, false)
                }
            )
        }
        composable(Destinations.Settings) {
            val coroutineScope = rememberCoroutineScope()
            SettingsScreen(
                onBack = { navController.navigateUp() },
                onLogout = {
                    coroutineScope.launch {
                        tokenStore.clearTokens()
                        navController.navigate(Destinations.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onChangePassword = { navController.navigate(Destinations.ChangePassword) }
            )
        }
        composable(Destinations.ChangePassword) {
            val viewModel = koinViewModel<ChangePasswordViewModel>()
            ChangePasswordScreen(
                onBack = { navController.navigateUp() },
                viewModel = viewModel
            )
        }
    }
}
