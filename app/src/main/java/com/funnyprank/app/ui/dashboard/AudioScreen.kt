package com.funnyprank.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextGreenSoft
import com.funnyprank.app.ui.theme.TextWhite
import java.util.Locale

@Composable
fun AudioScreen(viewModel: DashboardViewModel) {
    val audios by viewModel.audios.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val playback by viewModel.playback.collectAsState()

    val filtered = rememberFiltered(viewModel, audios, query)
    val playingItem = playback.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            DashboardScaffold(
                eyebrow = "Library",
                titlePrefix = "My ",
                titleAccent = "Audio",
                accentGreen = true,
                trailing = { CountBadge("${audios.size} FILES") }
            ) {}

        TextField(
            value = query,
            onValueChange = viewModel::setSearch,
            placeholder = { Text("Search audio files...", color = Color(0xFF626B74), fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = Color(0xFF626B74), modifier = Modifier.size(18.dp)) },
            singleLine = true,
            textStyle = TextStyle(color = TextWhite, fontSize = 12.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        )

        Spacer(Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Rounded.Headphones, null, tint = Color(0xFF59616A), modifier = Modifier.size(26.dp))
                Spacer(Modifier.height(10.dp))
                Text(
                    if (audios.isEmpty()) "No audio files yet. Add from Upload."
                    else "No matching audio found.",
                    color = TextGray, fontSize = 11.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 128.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    val isPlaying = playingItem?.id == item.id && playback.isPlaying
                    AudioRow(
                        item = item,
                        isPlaying = isPlaying,
                        onClick = { viewModel.playPreview(item) }
                    )
                }
            }
        }
        }

        NowPlayingBar(
            viewModel = viewModel,
            item = playingItem,
            isPlaying = playback.isPlaying,
            positionMs = playback.positionMs,
            durationMs = playback.durationMs,
            onToggle = { viewModel.toggleCurrent() },
            onStop = { viewModel.stopPlayback() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun rememberFiltered(
    viewModel: DashboardViewModel,
    audios: List<com.funnyprank.app.data.model.AudioItem>,
    query: String
): List<com.funnyprank.app.data.model.AudioItem> =
    androidx.compose.runtime.remember(audios, query) { viewModel.filteredAudios() }

@Composable
private fun AudioRow(
    item: com.funnyprank.app.data.model.AudioItem,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val accent = if (isPlaying) BrandGreen else BrandRed
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(9.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(if (isPlaying) Icons.Rounded.VolumeUp else Icons.Rounded.MusicNote, accent)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.display, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(Modifier.height(3.dp))
            Text(
                "${formatBytes(item.size)} • ${item.source}",
                color = Color(0xFF68717A), fontSize = 9.sp
            )
        }
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier
                .size(34.dp)
                .background(accent.copy(alpha = 0.15f), RoundedCornerShape(11.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = if (isPlaying) BrandGreen else TextWhite,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun NowPlayingBar(
    viewModel: DashboardViewModel,
    item: com.funnyprank.app.data.model.AudioItem?,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    onToggle: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Poll the real player position while playing so the progress bar stays live.
    var tickMs by remember { mutableLongStateOf(positionMs) }
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            tickMs = viewModel.currentPositionMs()
            kotlinx.coroutines.delay(250)
        }
        tickMs = positionMs
    }

    Box(modifier = modifier.fillMaxWidth()) {
        if (item != null) {
            val progress = if (durationMs > 0) (tickMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
            val finished = !isPlaying && durationMs > 0 && tickMs >= durationMs

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color(0xF20A0C0F), RoundedCornerShape(18.dp))
                    .padding(9.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        if (isPlaying) Icons.Rounded.VolumeUp else Icons.Rounded.MusicNote,
                        BrandGreen, size = 40
                    )
                    Spacer(Modifier.width(9.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            item.display, color = TextWhite, fontSize = 11.sp,
                            fontWeight = FontWeight.Bold, maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            when {
                                finished -> "Finished"
                                isPlaying -> "Playing"
                                else -> "Paused"
                            },
                            color = if (isPlaying) BrandGreen else TextGray, fontSize = 9.sp
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(11.dp))
                            .clickable(onClick = onToggle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            null, tint = BrandGreen, modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(11.dp))
                            .clickable(onClick = onStop),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Stop, null, tint = Color(0xFFFF657B), modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(9.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(listOf(BrandRed, BrandGreen))
                            )
                    )
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 KB"
    val units = arrayOf("B", "KB", "MB", "GB")
    var value = bytes.toDouble()
    var i = 0
    while (value >= 1024 && i < units.lastIndex) {
        value /= 1024
        i++
    }
    return String.format(Locale.US, if (i > 0) "%.1f %s" else "%.0f %s", value, units[i])
}
