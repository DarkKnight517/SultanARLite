package com.example.sultanarlite.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- Цветовые схемы ---
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF7B3F00),
    onPrimary = Color.White,
    background = Color(0xFFFFF8F3),
    onBackground = Color(0xFF1A0D0B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2D140F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFCE8B51),
    onPrimary = Color(0xFF332210),
    background = Color(0xFF1A0D0B),
    onBackground = Color(0xFFFFE4E1),
    surface = Color(0xFF2D140F),
    onSurface = Color(0xFFFFE4E1)
)

// --- Типографика ---
val Typography = Typography() // public val, чтобы не было конфликта

// --- Основная тема ---
@Composable
fun SultanARLiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Автоматически подхватывает системную тему
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
