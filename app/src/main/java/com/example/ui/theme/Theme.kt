package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val GalaxyDark = Color(0xFF03030F)
val GalaxyMidnight = Color(0xFF0C0A24)
val NeonCyan = Color(0xFF00F2FE)
val NeonMagenta = Color(0xFFEC008C)
val CosmicPurple = Color(0xFF8A2BE2)

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = CosmicPurple,
    tertiary = NeonMagenta,
    background = GalaxyDark,
    surface = GalaxyMidnight,
    onPrimary = GalaxyDark,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to true for immersive space theme
  dynamicColor: Boolean = false, // Turned off to avoid wallpaper color bleeding over galaxy space theme
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
