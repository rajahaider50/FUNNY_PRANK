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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.data.model.AudioItem
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextWhite
import java.util.Locale

@Composable
fun EditScreen(viewModel: DashboardViewModel) {
    val audios by viewModel.audios.collectAsState()

    var renaming by remember { mutableStateOf<AudioItem?>(null) }
    var deleting by remember { mutableStateOf<AudioItem?>(null) }
    var draft by remember { mutableStateOf("") }

    DashboardScaffold(
        eyebrow = "Manage Library",
        titlePrefix = "Edit ",
        titleAccent = "Audio",
        accentGreen = false,
        trailing = { CountBadge("${audios.size} FILES") }
    ) {
        if (audios.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Rounded.Edit, null, tint = Color(0xFF59616A), modifier = Modifier.size(26.dp))
                Spacer(Modifier.height(10.dp))
                Text("Your audio library is empty.", color = TextGray, fontSize = 11.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(audios, key = { it.id }) { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.045f), RoundedCornerShape(19.dp))
                            .padding(9.dp)
                    ) {
                        IconBox(Icons.Rounded.MusicNote, BrandRed)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.display, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Spacer(Modifier.height(3.dp))
                            Text("${formatBytes(item.size)} • ${item.source}", color = Color(0xFF68717A), fontSize = 9.sp)
                        }
                        Spacer(Modifier.width(6.dp))
                        SmallButton(Icons.Rounded.DriveFileRenameOutline, Color(0xFFDCE2E6)) {
                            draft = item.display
                            renaming = item
                        }
                        Spacer(Modifier.width(6.dp))
                        SmallButton(Icons.Rounded.DeleteOutline, Color(0xFFFF657B)) {
                            deleting = item
                        }
                    }
                }
            }
        }
    }

    renaming?.let { item ->
        AlertDialog(
            onDismissRequest = { renaming = null },
            title = { Text("Rename", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    singleLine = true,
                    label = { Text("New audio name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.rename(item.id, draft)
                    renaming = null
                }) { Text("Save", color = BrandGreen) }
            },
            dismissButton = {
                TextButton(onClick = { renaming = null }) { Text("Cancel", color = TextGray) }
            },
            containerColor = Color(0xFF101419)
        )
    }

    deleting?.let { item ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Delete audio?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("Remove \"${item.display}\" from your local library?", color = TextGray, fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(item)
                    deleting = null
                }) { Text("Delete", color = BrandRed) }
            },
            dismissButton = {
                TextButton(onClick = { deleting = null }) { Text("Cancel", color = TextGray) }
            },
            containerColor = Color(0xFF101419)
        )
    }
}

@Composable
private fun SmallButton(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .size(34.dp)
            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(11.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(16.dp))
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
