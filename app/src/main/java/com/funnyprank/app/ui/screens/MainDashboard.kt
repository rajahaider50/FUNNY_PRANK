package com.funnyprank.app.ui.screens

import android.provider.Settings
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.funnyprank.app.data.db.AppSettingsEntity
import com.funnyprank.app.floating.FloatingOverlayService
import com.funnyprank.app.ui.MainViewModel
import com.funnyprank.app.ui.components.GlassBackground
import com.funnyprank.app.ui.theme.GlassSurface
import com.funnyprank.app.ui.theme.AccentMid
import com.funnyprank.app.ui.theme.TextMuted
import com.funnyprank.app.ui.theme.TextPrimary
import com.funnyprank.app.ui.theme.TextSecondary

private enum class Tab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    UPLOAD("Upload", Icons.Filled.Upload),
    ADJUST("Adjust", Icons.Filled.Adjust),
    MORE("More", Icons.Filled.MoreHoriz)
}

/**
 * Main navigation shell with a premium glass bottom bar. Hosts Home / Upload /
 * Adjustment / More tabs.
 */
@Composable
fun MainDashboard(vm: MainViewModel, settings: AppSettingsEntity) {
    val context = LocalContext.current
    val sounds by vm.sounds.collectAsState()
    val favorites by vm.favorites.collectAsState()
    val currentPlayingId by vm.currentPlayingId.collectAsState()
    var selected by remember { mutableIntStateOf(0) }

    fun launchOverlay() {
        if (!Settings.canDrawOverlays(context)) {
            val intent = android.content.Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        } else {
            FloatingOverlayService.start(context)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 84.dp)
        ) {
            when (Tab.entries[selected]) {
                Tab.HOME -> HomeScreen(
                    sounds = sounds,
                    favorites = favorites,
                    currentPlayingId = currentPlayingId,
                    onLaunch = { launchOverlay() },
                    onPlay = { vm.play(it) },
                    onToggleFavorite = { vm.toggleFavorite(it) }
                )
                Tab.UPLOAD -> UploadScreen(
                    onImported = { _ -> }
                )
                Tab.ADJUST -> AdjustmentScreen(
                    settings = settings,
                    currentRouteLabel = vm.engine.currentRoute,
                    onSetOutputMode = { vm.setOutputMode(it) },
                    onSetVolume = { vm.setVolume(it) },
                    onOverlayToggle = {
                        vm.setOverlayEnabled(it)
                        if (it) launchOverlay()
                    }
                )
                Tab.MORE -> MoreScreen(vm)
            }
        }

        GlassBottomBar(
            selected = selected,
            onSelect = { selected = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun GlassBottomBar(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        GlassSurface(cornerRadius = 30.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Tab.entries.forEachIndexed { index, tab ->
                    val isSelected = selected == index
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelect(index) }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (isSelected) AccentMid.copy(alpha = 0.9f) else Color.Transparent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            tab.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) AccentMid else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

/**
 * "More" tab: app info and legal note. Remote, offline-first, no analytics.
 */
@Composable
private fun MoreScreen(vm: MainViewModel) {
    val context = LocalContext.current
    val currentRouteLabel by remember { mutableStateOf(vm.engine.currentRoute) }

    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("More", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Spacer(Modifier.height(20.dp))

            val rows = listOf(
                Pair("Volume", "Master output volume"),
                Pair("Output", currentRouteLabel ?: "…"),
                Pair("Privacy", "100% offline — no data leaves the device"),
                Pair("Sounds", "Manage & play funny prank sounds")
            )
            rows.forEach { (title, value) ->
                GlassSurface(cornerRadius = 20.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Funny Prank v1.0\nMade with 🤍 — sound pranks for friends",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
