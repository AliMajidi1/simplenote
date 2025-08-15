package com.example.simplenote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.R
import com.example.simplenote.data.AuthRepository
import com.example.simplenote.data.TokenStore
import com.example.simplenote.data.remote.AuthApi
import com.example.simplenote.ui.components.PrimaryPillButton
import com.example.simplenote.ui.screens.RegisterViewModel
import org.koin.compose.getKoin

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val tokenStore: TokenStore = getKoin().get()
    val authApi: AuthApi = getKoin().get()
    val viewModel = remember { RegisterViewModel(AuthRepository(authApi, tokenStore)) }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // AppBar
        TextButton(onClick = onBackToLogin) {
            Text(text = "← Back to Login")
        }

        // Title and Subtitle
        Text(text = "Register", style = MaterialTheme.typography.titleLarge)
        Text(text = "And start taking notes", style = MaterialTheme.typography.bodyMedium)

        // Input Fields
        OutlinedTextField(
            value = uiState.firstName,
            onValueChange = { viewModel.onFirstNameChange(it) },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF180E25),
                unfocusedTextColor = Color(0xFF180E25),
                errorTextColor = Color(0xFF180E25)
            )
        )

        OutlinedTextField(
            value = uiState.lastName,
            onValueChange = { viewModel.onLastNameChange(it) },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF180E25),
                unfocusedTextColor = Color(0xFF180E25),
                errorTextColor = Color(0xFF180E25)
            )
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF180E25),
                unfocusedTextColor = Color(0xFF180E25),
                errorTextColor = Color(0xFF180E25)
            )
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF180E25),
                unfocusedTextColor = Color(0xFF180E25),
                errorTextColor = Color(0xFF180E25)
            )
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF180E25),
                unfocusedTextColor = Color(0xFF180E25),
                errorTextColor = Color(0xFF180E25)
            )
        )

        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChange(it) },
            label = { Text("Retype Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF180E25),
                unfocusedTextColor = Color(0xFF180E25),
                errorTextColor = Color(0xFF180E25)
            )
        )

        // Register Button
        PrimaryPillButton(
            text = "Register",
            onClick = { viewModel.submit(onRegisterSuccess) },
            enabled = uiState.isSubmitEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            containerColor = Color(0xFF504EC3),
            contentColor = Color.White
        )

        // Footer
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = onBackToLogin) {
                Text(text = "Already have an account? Login here")
            }
        }
    }
}
