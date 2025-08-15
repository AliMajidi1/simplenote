package com.example.simplenote.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplenote.R
import com.example.simplenote.ui.components.PrimaryPillButton
import com.example.simplenote.ui.theme.SimplenoteTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val maxWidth = maxWidth
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.onboarding_illustration),
                    contentDescription = stringResource(R.string.cd_onboarding_illustration),
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.onboarding_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f))
                PrimaryPillButton(
                    text = stringResource(R.string.cta_lets_get_started),
                    onClick = onGetStarted,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    trailingIconSize = 28.dp
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(name = "Onboarding - Pixel 5 Light", showBackground = true, device = "id:pixel_5")
@Composable
fun PreviewOnboardingLight() {
    SimplenoteTheme(darkTheme = false) {
        OnboardingScreen(onGetStarted = {})
    }
}

@Preview(name = "Onboarding - Pixel 5 Dark", showBackground = true, device = "id:pixel_5")
@Composable
fun PreviewOnboardingDark() {
    SimplenoteTheme(darkTheme = true) {
        OnboardingScreen(onGetStarted = {})
    }
}

@Preview(name = "Onboarding - Small width", widthDp = 320, heightDp = 700, showBackground = true)
@Composable
fun PreviewOnboardingSmallWidth() {
    SimplenoteTheme(darkTheme = false) {
        OnboardingScreen(onGetStarted = {})
    }
}
