package com.example.munchly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light theme colors
private val LightPrimary = Color(0xFFD2691E)
private val LightPrimaryLight = Color(0x33D2691E)
private val LightBackground = Color(0xFFE8D5C4)
private val LightSurface = Color.White
private val LightTextPrimary = Color(0xFF8B4513)
private val LightTextSecondary = Color(0xFF8B7355)
private val LightTextPlaceholder = Color(0xFFB0A090)
private val LightError = Color(0xFFC62828)
private val LightErrorBackground = Color(0xFFFFEBEE)
private val LightBorderDefault = Color(0xFFE0E0E0)
private val LightButtonDisabled = Color(0xFFB0A090)

// Dark theme colors
private val DarkPrimary = Color(0xFFE67C34)
private val DarkPrimaryLight = Color(0x33E67C34)
private val DarkBackground = Color(0xFF1C1410)
private val DarkSurface = Color(0xFF2D2520)
private val DarkTextPrimary = Color(0xFFE8D5C4)
private val DarkTextSecondary = Color(0xFFB0A090)
private val DarkTextPlaceholder = Color(0xFF8B7355)
private val DarkError = Color(0xFFEF5350)
private val DarkErrorBackground = Color(0xFF4A1C1C)
private val DarkBorderDefault = Color(0xFF4A3F38)
private val DarkButtonDisabled = Color(0xFF5C5248)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    surface = LightSurface,
    background = LightBackground,
    error = LightError,
    onPrimary = Color.White,
    onSurface = LightTextPrimary,
    onBackground = LightTextPrimary,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    surface = DarkSurface,
    background = DarkBackground,
    error = DarkError,
    onPrimary = Color.White,
    onSurface = DarkTextPrimary,
    onBackground = DarkTextPrimary,
    onError = Color.White
)

object MunchlyColors {
    val primary: Color @Composable get() = MaterialTheme.colorScheme.primary
    val primaryLight: Color @Composable get() = if (isSystemInDarkTheme()) DarkPrimaryLight else LightPrimaryLight
    val background: Color @Composable get() = MaterialTheme.colorScheme.background
    val surface: Color @Composable get() = MaterialTheme.colorScheme.surface
    val textPrimary: Color @Composable get() = MaterialTheme.colorScheme.onSurface
    val textSecondary: Color @Composable get() = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary
    val textPlaceholder: Color @Composable get() = if (isSystemInDarkTheme()) DarkTextPlaceholder else LightTextPlaceholder
    val error: Color @Composable get() = MaterialTheme.colorScheme.error
    val errorBackground: Color @Composable get() = if (isSystemInDarkTheme()) DarkErrorBackground else LightErrorBackground
    val borderDefault: Color @Composable get() = if (isSystemInDarkTheme()) DarkBorderDefault else LightBorderDefault
    val buttonDisabled: Color @Composable get() = if (isSystemInDarkTheme()) DarkButtonDisabled else LightButtonDisabled
}

@Composable
fun MunchlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}