package com.example.simplenote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.simplenote.R
import com.example.simplenote.data.AuthRepository
import com.example.simplenote.data.TokenStore
import com.example.simplenote.data.remote.AuthApi
import com.example.simplenote.ui.components.PrimaryPillButton
import org.koin.compose.getKoin
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val tokenStore: TokenStore = getKoin().get()
    val authApi: AuthApi = getKoin().get()
    val viewModel = remember { RegisterViewModel(AuthRepository(authApi, tokenStore)) }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFFFFFFF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.Top
        ) {
            TextButton(onClick = onBackToLogin) {
                Text(text = stringResource(R.string.back_to_login))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.register_title),
                fontSize = 32.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                lineHeight = 38.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.register_subtitle),
                fontSize = 16.sp,
                color = Color(0xFF827D89),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // First Name
            Text(
                text = stringResource(R.string.first_name),
                fontSize = 16.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = { viewModel.onFirstNameChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.first_name),
                        fontSize = 16.sp,
                        color = Color(0xFFC8C5CB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF504EC3),
                    unfocusedBorderColor = Color(0xFFC8C5CB),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = Color(0xFF180E25),
                    unfocusedTextColor = Color(0xFF180E25),
                    errorTextColor = Color(0xFF180E25)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                isError = uiState.firstNameError != null
            )
            if (uiState.firstNameError != null) {
                Text(
                    text = uiState.firstNameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Last Name
            Text(
                text = stringResource(R.string.last_name),
                fontSize = 16.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = { viewModel.onLastNameChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.last_name),
                        fontSize = 16.sp,
                        color = Color(0xFFC8C5CB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF504EC3),
                    unfocusedBorderColor = Color(0xFFC8C5CB),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = Color(0xFF180E25),
                    unfocusedTextColor = Color(0xFF180E25),
                    errorTextColor = Color(0xFF180E25)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                isError = uiState.lastNameError != null
            )
            if (uiState.lastNameError != null) {
                Text(
                    text = uiState.lastNameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Username
            Text(
                text = stringResource(R.string.username),
                fontSize = 16.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.username),
                        fontSize = 16.sp,
                        color = Color(0xFFC8C5CB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF504EC3),
                    unfocusedBorderColor = Color(0xFFC8C5CB),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = Color(0xFF180E25),
                    unfocusedTextColor = Color(0xFF180E25),
                    errorTextColor = Color(0xFF180E25)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                isError = uiState.usernameError != null
            )
            if (uiState.usernameError != null) {
                Text(
                    text = uiState.usernameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Text(
                text = stringResource(R.string.email),
                fontSize = 16.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.email),
                        fontSize = 16.sp,
                        color = Color(0xFFC8C5CB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF504EC3),
                    unfocusedBorderColor = Color(0xFFC8C5CB),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = Color(0xFF180E25),
                    unfocusedTextColor = Color(0xFF180E25),
                    errorTextColor = Color(0xFF180E25)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                isError = uiState.emailError != null
            )
            if (uiState.emailError != null) {
                Text(
                    text = uiState.emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Password
            Text(
                text = stringResource(R.string.password),
                fontSize = 16.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.password),
                        fontSize = 16.sp,
                        color = Color(0xFFC8C5CB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF504EC3),
                    unfocusedBorderColor = Color(0xFFC8C5CB),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = Color(0xFF180E25),
                    unfocusedTextColor = Color(0xFF180E25),
                    errorTextColor = Color(0xFF180E25)
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                isError = uiState.passwordError != null
            )
            if (uiState.passwordError != null) {
                Text(
                    text = uiState.passwordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            Text(
                text = stringResource(R.string.confirm_password),
                fontSize = 16.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.confirm_password),
                        fontSize = 16.sp,
                        color = Color(0xFFC8C5CB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF504EC3),
                    unfocusedBorderColor = Color(0xFFC8C5CB),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = Color(0xFF180E25),
                    unfocusedTextColor = Color(0xFF180E25),
                    errorTextColor = Color(0xFF180E25)
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                isError = uiState.confirmPasswordError != null
            )
            if (uiState.confirmPasswordError != null) {
                Text(
                    text = uiState.confirmPasswordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            PrimaryPillButton(
                text = stringResource(R.string.register_cta),
                onClick = { viewModel.submit(onRegisterSuccess) },
                enabled = uiState.isSubmitEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                containerColor = Color(0xFF504EC3),
                contentColor = Color.White
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onBackToLogin) {
                    Text(text = stringResource(R.string.already_have_account))
                }
            }
        }
    }
}
