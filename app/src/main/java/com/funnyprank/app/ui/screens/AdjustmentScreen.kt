package com.funnyprank.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.funnyprank.app.data.db.SoundItem
import com.funnyprank.app.data.db.AppSettingsEntity
import com.funnyprank.app.ui.components.GlassBackground
import com.funnyprank.app.ui.theme.GlassSurface
import com.funnyprank.app.ui.theme.AccentMid
import com.funnyprank.app.ui.theme.AccentPink
import com.funnyprank.app.ui.theme.GlassWhite
import com.funnyprank.app.ui.theme.TextPrimary
import com.funnyprank.app.ui.theme.TextSecondary

/**
 * Adjustment tab: output mode (Auto / Speaker / Wired / Bluetooth), master
 * volume, and overlay enable toggle.
 */
@Composable
fun AdjustmentScreen(
    settings: AppSettingsEntity,
    currentRouteLabel: String?,
    onSetOutputMode: (String) -> Unit,
    onSetVolume: (Float) -> Unit,
    onOverlayToggle: (Boolean) -> Unit
) {
    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Adjustments", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(
                currentRouteLabel?.let { "Detected output: $it" } ?: "Detecting audio output…",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(24.dp))

            Text("Output Mode", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModeChip("AUTO", "Auto", Icons.Filled.Smartphone, settings.outputMode == "AUTO") { onSetOutputMode("AUTO") }
                ModeChip("SPEAKER", "Speaker", Icons.Filled.Smartphone, settings.outputMode == "SPEAKER") { onSetOutputMode("SPEAKER") }
                ModeChip("WIRED", "Wired", Icons.Filled.Headset, settings.outputMode == "WIRED") { onSetOutputMode("WIRED") }
                ModeChip("BLUETOOTH", "Bluetooth", Icons.Filled.Bluetooth, settings.outputMode == "BLUETOOTH") { onSetOutputMode("BLUETOOTH") }
            }

            Spacer(Modifier.height(28.dp))

            GlassSurface(cornerRadius = 24.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = AccentMid)
                        Text("Volume", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    }
                    Slider(
                        value = settings.volumeBoost,
                        onValueChange = { onSetVolume(it) },
                        valueRange = 0f..1f
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassSurface(cornerRadius = 24.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Filled.Mic, contentDescription = null, tint = AccentPink)
                        Column {
                            Text("Overlay Soundboard", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text("Floating bubble over other apps", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                    Switch(
                        checked = settings.overlayEnabled,
                        onCheckedChange = onOverlayToggle
                    )
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.ModeChip(
    value: String,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(if (selected) AccentMid.copy(alpha = 0.35f) else GlassWhite, CircleShape)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) Color.White else TextSecondary)
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (selected) Color.White else TextSecondary)
    }
}
