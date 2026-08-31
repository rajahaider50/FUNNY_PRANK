package com.funnyprank.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FunnyPrankColors = darkColorScheme(
    primary = BrandRed,
    onPrimary = Color.White,
    secondary = BrandGreen,
    onSecondary = Color(0xFF03130B),
    tertiary = BrandRedDeep,
    background = BgBlack,
    onBackground = TextWhite,
    surface = BgBlackSoft,
    onSurface = TextWhite,
    surfaceVariant = Color(0xFF101316),
    onSurfaceVariant = TextGray,
    error = Color(0xFFFF4D6D),
    onError = Color.White,
    outline = GlassBorder
)

@Composable
fun FunnyPrankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FunnyPrankColors,
        typography = Typography,
        content = content
    )
}
