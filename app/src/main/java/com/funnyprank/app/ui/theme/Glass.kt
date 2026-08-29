package com.funnyprank.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable glassmorphism building blocks: frosted translucent surfaces with a
 * soft outer glow, a subtle border, and optional bright accent edge.
 */

@Composable
fun Modifier.glass(
    cornerRadius: Dp = 28.dp,
    borderColor: Color = GlassBorder,
    blurAmount: Dp = 0.dp
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .clip(shape)
        .background(GlassWhite)
        .border(width = 1.dp, color = borderColor, shape = shape)
}

/**
 * A frosted glass surface with a faint top-left highlight gradient.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    borderColor: Color = GlassBorder,
    inside: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .glass(cornerRadius = cornerRadius, borderColor = borderColor)
    ) {
        // top sheen
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )
        inside()
    }
}

/**
 * A round glass button — used for the big center launch + small utility taps.
 */
@Composable
fun GlassCircle(
    modifier: Modifier = Modifier,
    size: Dp,
    borderColor: Color = GlassBorder,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .then(Modifier.size(size))
            .clip(CircleShape)
            .background(GlassWhite)
            .border(1.dp, borderColor, CircleShape),
        content = { content() }
    )
}

/**
 * Adds a soft neon glow halo behind any composable (for the launch button).
 */
@Composable
fun GlowHalo(
    color: Color,
    modifier: Modifier = Modifier,
    blurRadius: Dp = 32.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        Box(
            Modifier
                .matchParentSize()
                .background(color.copy(alpha = 0.55f), CircleShape)
                .blur(blurRadius)
        )
        content()
    }
}
