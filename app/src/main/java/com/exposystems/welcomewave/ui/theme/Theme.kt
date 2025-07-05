package com.exposystems.welcomewave.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ExpoPurple,
    secondary = ExpoDarkGrey,
    tertiary = ExpoLightGrey
)

private val LightColorScheme = lightColorScheme(
    primary = ExpoPurple,
    secondary = ExpoDarkGrey,
    tertiary = ExpoLightGrey
)

@Composable
fun WelcomeWaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}