package com.funnyprank.app.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.funnyprank.app.ui.MainViewModel
import com.funnyprank.app.ui.components.GlassBackground
import com.funnyprank.app.ui.theme.AccentMid
import com.funnyprank.app.ui.theme.GlassSurface
import com.funnyprank.app.ui.theme.GlassWhite
import com.funnyprank.app.ui.theme.GlassBorder
import com.funnyprank.app.ui.theme.TextPrimary
import com.funnyprank.app.ui.theme.TextSecondary
import com.funnyprank.app.ui.theme.Success

/**
 * Host that wires runtime permission requests to the UI and, once the user is
 * ready, marks onboarding complete.
 */
@Composable
fun PermissionHost(vm: MainViewModel) {
    val context = LocalContext.current

    var refresh by remember { mutableStateOf(0) }
    refresh // keep refresh read so recomposition updates statuses

    fun hasOverlay() = Settings.canDrawOverlays(context)
    fun hasNotif(): Boolean = Build.VERSION.SDK_INT < 33 ||
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    fun hasMic(): Boolean = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    fun hasBt(): Boolean {
        if (Build.VERSION.SDK_INT < 23) return true
        return context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    val runtimeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { refresh++ }

    fun requestRuntime() {
        val perms = buildList {
            if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
            if (Build.VERSION.SDK_INT >= 23) add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= 31) add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        runtimeLauncher.launch(perms.toTypedArray())
    }

    PermissionScreen(
        isOverlayGranted = hasOverlay(),
        isNotificationGranted = hasNotif(),
        isMicGranted = hasMic(),
        isBluetoothGranted = hasBt(),
        onRequestOverlay = {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
            )
            refresh++
        },
        onRequestNotification = { requestRuntime() },
        onRequestMic = { requestRuntime() },
        onRequestBluetooth = { requestRuntime() },
        onContinue = { vm.markOnboardingDone() }
    )
}

/**
 * First-run onboarding: explains and requests the key permissions each on its
 * own glass card, with a clear "Let's Go" action at the end.
 */
@Composable
fun PermissionScreen(
    isOverlayGranted: Boolean,
    isNotificationGranted: Boolean,
    isMicGranted: Boolean,
    isBluetoothGranted: Boolean,
    onRequestOverlay: () -> Unit,
    onRequestNotification: () -> Unit,
    onRequestMic: () -> Unit,
    onRequestBluetooth: () -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current

    fun canRequestNotification() =
        Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = "Get Ready 🤣",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary
            )
            Text(
                text = "A few quick permissions to power up your soundboard",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(24.dp))

            PermissionCard(
                icon = Icons.Filled.Notifications,
                title = "Notifications",
                subtitle = "Keeps the soundboard alive in the background",
                granted = isNotificationGranted,
                onClick = onRequestNotification
            )
            Spacer(Modifier.height(12.dp))

            PermissionCard(
                icon = Icons.Filled.VolumeUp,
                title = "Display over other apps",
                subtitle = "Shows the floating sound bubble on top of games",
                granted = isOverlayGranted,
                onClick = onRequestOverlay
            )
            Spacer(Modifier.height(12.dp))

            PermissionCard(
                icon = Icons.Filled.Mic,
                title = "Microphone",
                subtitle = "Optional — only for recording new sounds",
                granted = isMicGranted,
                onClick = onRequestMic
            )
            Spacer(Modifier.height(12.dp))

            PermissionCard(
                icon = Icons.Filled.Bluetooth,
                title = "Bluetooth",
                subtitle = "Optional — for routing audio to a headset",
                granted = isBluetoothGranted,
                onClick = onRequestBluetooth
            )

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = AccentMid)
            ) {
                Text("Let's Go 🚀", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    granted: Boolean,
    onClick: () -> Unit
) {
    GlassSurface(cornerRadius = 22.dp) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(GlassWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = AccentMid)
                }
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            if (granted) {
                Text("✓", color = Success, fontWeight = FontWeight.Black)
            } else {
                IconButton(onClick = onClick) {
                    Text("Allow", color = AccentMid, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
