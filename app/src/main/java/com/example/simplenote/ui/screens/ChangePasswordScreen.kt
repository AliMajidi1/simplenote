package com.example.simplenote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplenote.ui.components.PrimaryPillButton
import com.example.simplenote.ui.theme.Purple700

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    viewModel: ChangePasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var retypePasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Purple700)
                    }
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        },
        backgroundColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Please input your current password first",
                color = Purple700,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("Current Password", color = Color.Black, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.currentPassword.collectAsState().value,
                onValueChange = {
                    viewModel.currentPassword.value = it
                    viewModel.clearError()
                },
                placeholder = { Text("********") },
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White
                )
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = "Now, create your new password",
                color = Purple700,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("New Password", color = Color.Black, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.newPassword.collectAsState().value,
                onValueChange = {
                    viewModel.newPassword.value = it
                    viewModel.clearError()
                },
                placeholder = { Text("********") },
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White
                )
            )
            Text(
                text = "Password should contain a-z, A-Z, 0-9",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
            Text("Retype New Password", color = Color.Black, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.retypePassword.collectAsState().value,
                onValueChange = {
                    viewModel.retypePassword.value = it
                    viewModel.clearError()
                },
                placeholder = { Text("********") },
                visualTransformation = if (retypePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            if (uiState is ChangePasswordUiState.Error) {
                Text(
                    text = (uiState as ChangePasswordUiState.Error).error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            PrimaryPillButton(
                text = "Submit New Password",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.submitChangePassword()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState !is ChangePasswordUiState.Loading
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


