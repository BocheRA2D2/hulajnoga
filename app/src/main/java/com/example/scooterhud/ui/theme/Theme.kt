package com.example.scooterhud.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary    = DarkPrimary,
    background = DarkBackground,
    surface    = DarkSurface,
    onBackground = DarkOnBg,
    onSurface  = DarkOnBg,
    secondary  = DarkAccent
)

private val LightColorScheme = lightColorScheme(
    primary    = LightPrimary,
    background = LightBackground,
    surface    = LightSurface,
    onBackground = LightOnBg,
    onSurface  = LightOnBg,
    secondary  = LightAccent
)

@Composable
fun ScooterHudTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
