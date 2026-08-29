package com.funnyprank.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = AccentMid,
    onPrimary = Color.White,
    secondary = AccentPink,
    onSecondary = Color.White,
    tertiary = AccentPurple,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = Color(0xFF141A38),
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF1D2547),
    error = Danger,
    onError = Color.White,
    outline = GlassBorder
)

@Composable
fun FunnyPrankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}
