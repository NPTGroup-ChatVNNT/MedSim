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

private val DarkColorScheme = darkColorScheme(
    primary = MedTealPrimary,
    onPrimary = Color.Black,
    primaryContainer = MedTealDark,
    onPrimaryContainer = Color.White,
    secondary = MedCyanGlow,
    onSecondary = Color.Black,
    tertiary = MedEcgGreen,
    background = MedDeepBackground,
    onBackground = MedTextPrimary,
    surface = MedSurfaceDark,
    onSurface = MedTextPrimary,
    surfaceVariant = MedCardStroke,
    onSurfaceVariant = MedTextSecondary,
    error = MedCriticalRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MedLightPrimary,
    onPrimary = Color.White,
    secondary = MedTealDark,
    onSecondary = Color.White,
    tertiary = MedEcgGreen,
    background = MedLightBackground,
    onBackground = Color(0xFF0F172A),
    surface = MedLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = MedLightCard,
    onSurfaceVariant = Color(0xFF475569),
    error = MedCriticalRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to clinical dark for monitor/medical look
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
