package com.example.simplenote.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.R
import com.example.simplenote.ui.components.PrimaryPillButton
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateHome: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginEvent.NavigateHome -> onNavigateHome()
                is LoginEvent.NavigateRegister -> onNavigateRegister()
            }
        }
    }

    LaunchedEffect(uiState.apiError) {
        uiState.apiError?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

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
            Text(
                text = stringResource(R.string.login_title),
                fontSize = 32.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                lineHeight = 38.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.login_subtitle),
                fontSize = 16.sp,
                color = Color(0xFF827D89),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.email_label),
                fontSize = 16.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.email_placeholder),
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
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
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
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.password_label),
                fontSize = 16.sp,
                color = Color(0xFF180E25),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.password_placeholder),
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
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (uiState.isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(painterResource(icon), contentDescription = stringResource(R.string.toggle_password_visibility))
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.submit()
                    }
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
            Spacer(modifier = Modifier.height(40.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                PrimaryPillButton(
                    text = stringResource(R.string.login_button),
                    onClick = { viewModel.submit() },
                    enabled = !uiState.isLoading && uiState.email.isNotBlank() && uiState.password.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    containerColor = Color(0xFF504EC3),
                    contentColor = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(15.dp).fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFEFEEF0))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.or_label),
                    fontSize = 12.sp,
                    color = Color(0xFF827D89),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFEFEEF0))
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onNavigateRegister) {
                    Text(text = "Don't have an account? Register here")
                }
            }
        }
    }
}
