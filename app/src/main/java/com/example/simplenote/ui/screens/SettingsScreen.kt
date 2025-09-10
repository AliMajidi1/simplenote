package com.example.simplenote.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.BuildConfig
import com.example.simplenote.R
import com.example.simplenote.data.remote.NetworkResult
import com.example.simplenote.ui.screens.SettingsViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.window.Dialog

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel(), onBack: () -> Unit, onLogout: () -> Unit, onChangePassword: () -> Unit) {
    val uiState = viewModel.uiState.collectAsState()
    val showLogoutDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserInfo()
    }

    val (name, email) = when (val state = uiState.value) {
        is SettingsUiState.Success -> {
            val user = state.user
            val fullName = listOf(user.first_name, user.last_name).filter { it.isNotBlank() }.joinToString(" ")
            fullName to user.email
        }
        is SettingsUiState.Error -> "Error" to state.message
        is SettingsUiState.Loading -> "..." to "..."
    }

    if (showLogoutDialog.value) {
        Dialog(onDismissRequest = { showLogoutDialog.value = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                        .widthIn(min = 280.dp, max = 340.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Log Out",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A)),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Are you sure you want to log out from the application?",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF757575)),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { showLogoutDialog.value = false },
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6C47FF)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Cancel", color = Color(0xFF6C47FF))
                        }
                        Button(
                            onClick = {
                                showLogoutDialog.value = false
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C47FF)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Yes", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF6C47FF))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                color = Color.LightGray
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    tint = Color(0xFFB0B0B0),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB0B0B0), fontSize = 14.sp)
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp, horizontal = 24.dp))

        Text(
            text = "APP SETTINGS",
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB0B0B0), fontWeight = FontWeight.Medium, fontSize = 12.sp),
            modifier = Modifier.padding(start = 24.dp, top = 4.dp, bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onChangePassword() }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Change Password",
                tint = Color(0xFF1A1A1A),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Chevron",
                tint = Color(0xFFB0B0B0),
                modifier = Modifier.size(20.dp)
            )
        }
        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLogoutDialog.value = true }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Log Out",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Log Out",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFD32F2F), fontWeight = FontWeight.Medium)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Simple Notes v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB0B0B0), fontSize = 12.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
    }
}