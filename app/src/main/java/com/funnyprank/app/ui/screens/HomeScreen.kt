package com.funnyprank.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.funnyprank.app.data.db.SoundItem
import com.funnyprank.app.ui.components.GlassBackground
import com.funnyprank.app.ui.components.LightningLaunchButton
import com.funnyprank.app.ui.components.SoundGridItem
import com.funnyprank.app.ui.theme.AccentPink
import com.funnyprank.app.ui.theme.TextMuted
import com.funnyprank.app.ui.theme.TextPrimary

/**
 * Main dashboard home tab: hero launch button + favorite (pinned) section +
 * all sounds in a professional responsive grid.
 */
@Composable
fun HomeScreen(
    sounds: List<SoundItem>,
    favorites: List<SoundItem>,
    currentPlayingId: Long?,
    onLaunch: () -> Unit,
    onPlay: (SoundItem) -> Unit,
    onToggleFavorite: (SoundItem) -> Unit
) {
    GlassBackground {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero launch button spans both columns
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FUNNY PRANK",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tap to launch the floating soundboard",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                    Spacer(Modifier.height(18.dp))
                    LightningLaunchButton(onClick = onLaunch)
                }
            }

            if (favorites.isNotEmpty()) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = AccentPink,
                            modifier = Modifier.height(18.dp)
                        )
                        androidx.compose.material3.Text(
                            text = "  Pinned Favorites",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                items(favorites, key = { it.id }) { item ->
                    SoundGridItem(
                        item = item,
                        isPlaying = currentPlayingId == item.id,
                        onClick = { onPlay(item) },
                        onToggleFavorite = { onToggleFavorite(item) }
                    )
                }
            }

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Text(
                    text = "All Sounds",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }
            items(sounds, key = { it.id }) { item ->
                SoundGridItem(
                    item = item,
                    isPlaying = currentPlayingId == item.id,
                    onClick = { onPlay(item) },
                    onToggleFavorite = { onToggleFavorite(item) }
                )
            }
        }
    }
}
