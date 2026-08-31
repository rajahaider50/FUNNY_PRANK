package com.funnyprank.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextWhite

@Composable
fun SettingsScreen(viewModel: DashboardViewModel) {
    val darkTheme by remember { mutableStateOf(viewModel.settings.darkTheme) }
    val audioPreview by remember { mutableStateOf(viewModel.settings.audioPreview) }

    var activeTab by remember { mutableStateOf<InfoTab?>(null) }

    DashboardScaffold(
        eyebrow = "App Control",
        titlePrefix = "Set",
        titleAccent = "tings",
        accentGreen = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingToggleRow(Icons.Rounded.Palette, "Dark Glass Theme",
                    "Premium black glass UI with red/green accents.",
                    initial = darkTheme,
                    onChange = { viewModel.setDarkTheme(it) })

                SettingToggleRow(Icons.Rounded.VolumeUp, "Audio Preview",
                    "Enable playback inside the app.",
                    initial = audioPreview,
                    onChange = { viewModel.setAudioPreview(it) })

                NavRow(Icons.Rounded.Notifications, "Support Center",
                    "Help and troubleshooting.") { activeTab = InfoTab.SUPPORT }
                NavRow(Icons.Rounded.Mail, "Contact Center",
                    "Contact action for the final APK.") { activeTab = InfoTab.CONTACT }
                NavRow(Icons.Rounded.Edit, "Privacy",
                    "Audio is kept in local device storage.") { activeTab = InfoTab.PRIVACY }
            }

            Spacer(Modifier.height(12.dp))

            GlassBox(modifier = Modifier.fillMaxWidth().padding(17.dp), cornerRadius = 24) {
                Column {
                    Text("Funny Prank", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Premium local audio soundboard. All audio is copied into app-private storage and never leaves your device.",
                        color = TextGray, fontSize = 10.sp, lineHeight = 14.sp
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    activeTab?.let { tab -> InfoDialog(tab) { activeTab = null } }
}

private enum class InfoTab { SUPPORT, CONTACT, PRIVACY }

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    desc: String,
    initial: Boolean,
    onChange: (Boolean) -> Unit
) {
    var on by remember { mutableStateOf(initial) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.045f), RoundedCornerShape(19.dp))
            .padding(9.dp)
    ) {
        IconBox(icon, BrandGreen, size = 41)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(desc, color = Color(0xFF68717A), fontSize = 9.sp, lineHeight = 12.sp)
        }
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(24.dp)
                .clip(CircleShape)
                .background(if (on) BrandGreen.copy(alpha = 0.3f) else Color(0xFF20242A))
                .clickable {
                    on = !on
                    onChange(on)
                }
                .padding(3.dp),
            contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (on) BrandGreen else Color(0xFF858B91))
            )
        }
    }
}

@Composable
private fun NavRow(icon: ImageVector, title: String, desc: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.045f), RoundedCornerShape(19.dp))
            .clickable(onClick = onClick)
            .padding(9.dp)
    ) {
        IconBox(icon, BrandRed, size = 41)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(desc, color = Color(0xFF68717A), fontSize = 9.sp, lineHeight = 12.sp)
        }
        Icon(Icons.Rounded.ArrowForwardIos, null, tint = Color(0xFF69727A), modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun InfoDialog(tab: InfoTab, onDismiss: () -> Unit) {
    val (title, body) = when (tab) {
        InfoTab.SUPPORT -> "Support Center" to
            "Need help with Funny Prank?\n\n• Add audio from the Upload tab (single file, ZIP or folder).\n" +
            "• Play audio from the Audio tab — enable Audio Preview in Settings.\n" +
            "• Floating control needs the 'Display over other apps' permission, granted via Launch.\n\n" +
            "Troubleshooting content will be added here."
        InfoTab.CONTACT -> "Contact Center" to
            "Contact actions for the final APK will be configured here.\n\nPlaceholder — no message is sent in this build."
        InfoTab.PRIVACY -> "Privacy" to
            "Audio privacy:\n\nFunny Prank is fully local-first. Audio you import is copied into the app's private storage and never uploaded to any server. No analytics or tracking are included."
    }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = TextWhite, fontWeight = FontWeight.Bold) },
        text = { Text(body, color = TextGray, fontSize = 12.sp, lineHeight = 17.sp) },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text("OK", color = BrandGreen) }
        },
        containerColor = Color(0xFF101419)
    )
}
