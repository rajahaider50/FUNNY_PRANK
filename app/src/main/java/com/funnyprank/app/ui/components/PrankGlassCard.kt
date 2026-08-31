package com.funnyprank.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.funnyprank.app.ui.theme.GlassBorder
import com.funnyprank.app.ui.theme.GlassHi
import com.funnyprank.app.ui.theme.GlassLow

/**
 * Premium frosted-glass card for the black/red/green design system.
 */
@Composable
fun PrankGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    borderColor: Color = GlassBorder,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(GlassHi, GlassLow),
                    start = Offset(0f, 0f),
                    end = Offset(1400f, 1400f)
                )
            )
            .border(1.dp, borderColor, shape)
    ) {
        // top inner highlight
        Box(
            Modifier
                .matchParentSize()
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)
                    )
                )
        )
        content()
    }
}
