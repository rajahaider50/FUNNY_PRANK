package com.funnyprank.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.funnyprank.app.ui.theme.DeepNavy
import com.funnyprank.app.ui.theme.GlassBackgroundBrush
import com.funnyprank.app.ui.theme.MidNavy
import kotlin.random.Random

/**
 * Full-screen luxury glass ambience: a navy gradient base with floating
 * blurred neon orbs (violet, cyan, pink) for that premium glow.
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GlassBackgroundBrush)
    ) {
        // Floating neon orbs
        Canvas(Modifier.fillMaxSize()) {
            val orbs = listOf(
                Triple(Color(0xFF7C4DFF), Offset(size.width * 0.1f, size.height * 0.08f), 220f),
                Triple(Color(0xFF00C2FF), Offset(size.width * 0.9f, size.height * 0.2f), 180f),
                Triple(Color(0xFFFF3D8E), Offset(size.width * 0.75f, size.height * 0.85f), 200f),
                Triple(Color(0xFF00E5FF), Offset(size.width * 0.15f, size.height * 0.9f), 170f)
            )
            orbs.forEach { (c, center, radius) ->
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(c.copy(alpha = 0.45f), c.copy(alpha = 0f))
                    ),
                    radius = radius,
                    center = center
                )
            }
        }
        content()
    }
}
