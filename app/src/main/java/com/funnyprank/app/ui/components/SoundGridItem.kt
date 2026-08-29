package com.funnyprank.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.funnyprank.app.data.db.SoundItem
import com.funnyprank.app.ui.theme.AccentMid
import com.funnyprank.app.ui.theme.AccentPink
import com.funnyprank.app.ui.theme.AccentPurple
import com.funnyprank.app.ui.theme.GlassSurface
import com.funnyprank.app.ui.theme.GlassWhite
import com.funnyprank.app.ui.theme.TextMuted
import com.funnyprank.app.ui.theme.TextPrimary

/**
 * A single "audio box" tile: play button glowing, name below, favorite star.
 * Tapping the card plays/pauses; tapping the star toggles favorite (pins to top).
 */
@Composable
fun SoundGridItem(
    item: SoundItem,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    GlassSurface(
        cornerRadius = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.86f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(GlassWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "favorite",
                        tint = if (item.isFavorite) AccentPink else TextMuted,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onToggleFavorite() }
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            // Play button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.linearGradient(listOf(AccentMid, AccentPurple, AccentPink)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "pause" else "play",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = item.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = formatDuration(item.durationMs),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

private fun formatDuration(ms: Long): String {
    val s = ms / 1000
    return if (s <= 0) "" else "%d:%02d".format(s / 60, s % 60)
}
