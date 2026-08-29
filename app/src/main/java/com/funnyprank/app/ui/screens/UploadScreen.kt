package com.funnyprank.app.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.funnyprank.app.data.db.SoundItem
import com.funnyprank.app.ui.components.GlassBackground
import com.funnyprank.app.ui.components.GlassSurface
import com.funnyprank.app.ui.theme.AccentMid
import com.funnyprank.app.ui.theme.GlassWhite
import com.funnyprank.app.ui.theme.TextPrimary
import com.funnyprank.app.ui.theme.TextSecondary
import com.funnyprank.app.import.SoundImporter
import kotlinx.coroutines.launch

/**
 * Upload tab: import a single audio file, a whole folder, or a ZIP that gets
 * auto-extracted. All imported files are copied into app-specific storage and
 * registered in Room.
 */
@Composable
fun UploadScreen(
    onImported: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val singleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val count = SoundImporter.importUris(context, listOf(it))
                onImported(count)
            }
        }
    }

    val folderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val count = SoundImporter.importTree(context, it)
                onImported(count)
            }
        }
    }

    val zipLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val count = SoundImporter.importZip(context, it)
                onImported(count)
            }
        }
    }

    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Add Sounds", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Text(
                "Import audio from your device — files are stored locally (100% offline)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(28.dp))

            UploadOption(
                icon = Icons.Filled.FilePresent,
                title = "Single Audio File",
                subtitle = "Pick one mp3, wav, ogg or m4a file",
                onClick = { singleLauncher.launch(arrayOf("audio/*", "application/ogg", "audio/x-m4a")) }
            )
            Spacer(Modifier.height(14.dp))

            UploadOption(
                icon = Icons.Filled.Folder,
                title = "Whole Folder",
                subtitle = "Import every audio inside a folder",
                onClick = { folderLauncher.launch(null) }
            )
            Spacer(Modifier.height(14.dp))

            UploadOption(
                icon = Icons.Filled.FolderZip,
                title = "ZIP File",
                subtitle = "Auto-extract all audios from a ZIP",
                onClick = { zipLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed", "*/*")) }
            )

            Spacer(Modifier.height(32.dp))
            GlassSurface(cornerRadius = 22.dp, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "💡 All sounds stay on your device. No internet, no accounts, no cloud.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun UploadOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    GlassSurface(cornerRadius = 24.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(GlassWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AccentMid, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}
