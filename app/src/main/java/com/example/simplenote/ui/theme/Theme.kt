package com.example.simplenote.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Primary,
    surface = Primary,
    onPrimary = NeutralWhite,
    onSecondary = NeutralBlack,
    onBackground = NeutralWhite,
    onSurface = NeutralWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Primary,
    surface = Primary,
    onPrimary = NeutralWhite,
    onSecondary = NeutralBlack,
    onBackground = NeutralWhite,
    onSurface = NeutralWhite,
)

@Composable
fun SimplenoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}