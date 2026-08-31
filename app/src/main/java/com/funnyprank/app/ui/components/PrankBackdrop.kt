package com.funnyprank.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.funnyprank.app.ui.theme.AppBackgroundBrush
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BgBlackDeep

/**
 * Shared premium dark backdrop for the whole app.
 * Layers (bottom -> top):
 *  1. deep black/green gradient   (the brand gradient)
 *  2. red ambient glow  top-left
 *  3. green ambient glow bottom-right
 *  4. subtle center light
 */
@Composable
fun PrankBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(AppBackgroundBrush)) {
        // ambient glows
        Canvas(Modifier.fillMaxSize()) {
            fun glow(color: Color, center: Offset, radius: Float, alpha: Float) {
                val shader: Shader = RadialGradientShader(
                    center = center,
                    radius = radius,
                    colors = listOf(color.copy(alpha = alpha), color.copy(alpha = 0f))
                )
                drawRect(brush = ShaderBrush(shader))
            }
            glow(BrandRed, Offset(size.width * 0.12f, size.height * 0.06f), size.width * 0.9f, 0.16f)
            glow(BrandGreen, Offset(size.width * 0.9f, size.height * 0.95f), size.width * 0.95f, 0.13f)
            glow(BgBlackDeep, Offset(size.width * 0.5f, size.height * 0.5f), size.width * 0.6f, 0.35f)
        }
        content()
    }
}
