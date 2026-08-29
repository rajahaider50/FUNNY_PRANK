package com.funnyprank.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.funnyprank.app.ui.theme.AccentMid
import com.funnyprank.app.ui.theme.AccentPink
import com.funnyprank.app.ui.theme.AccentPurple
import com.funnyprank.app.ui.theme.GlassBorder
import com.funnyprank.app.ui.theme.TextPrimary

/**
 * The centerpiece home button: a large round premium lightning launch button
 * with a continuous pulsing glow halo. Tapping it launches the floating overlay.
 */
@Composable
fun LightningLaunchButton(
    onClick: () -> Unit,
    label: String = "Launch",
    size: Int = 190,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "launch pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val halo by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )

    val sizeDp = size.dp
    val btn = size * 0.82f

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing outer halo
        Box(
            Modifier
                .size(sizeDp)
                .background(
                    Brush.radialGradient(
                        listOf(AccentPurple.copy(alpha = halo + 0.12f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // Main glass button
        Box(
            modifier = Modifier
                .size(btn.dp)
                .background(GlassBorder.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(btn.dp)
                    .border(2.dp, GlassBorder, CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AccentMid, AccentPurple, AccentPink)),
                        CircleShape
                    )
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size((btn * 0.24f).dp)
                )
            }
        }
    }
}
