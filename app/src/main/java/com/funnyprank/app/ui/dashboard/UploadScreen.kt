package com.funnyprank.app.ui.dashboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilePresent
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnyprank.app.ui.theme.BrandGreen
import com.funnyprank.app.ui.theme.BrandRed
import com.funnyprank.app.ui.theme.TextGray
import com.funnyprank.app.ui.theme.TextGreenSoft
import com.funnyprank.app.ui.theme.TextWhite
import kotlinx.coroutines.launch

private data class UploadCardUi(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color,
    val click: () -> Unit
)

@Composable
fun UploadScreen(
    viewModel: DashboardViewModel,
    onOpenLibrary: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var statusText by remember { mutableStateOf("Ready. Choose a file, ZIP or folder.") }
    var statusStrong by remember { mutableStateOf("Ready.") }
    var working by remember { mutableStateOf(false) }

    val singlePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            working = true
            statusStrong = "Importing..."
            statusText = "Copying audio into the app library."
            scope.launch {
                val r = viewModel.repository.importSingle(uri)
                working = false
                statusStrong = "Import complete."
                statusText = "${r.added} new audio file(s) stored locally. (${r.duplicates} duplicate/skipped)"
                viewModel.toast("${r.added} audio file(s) imported")
            }
        }
    }

    val zipPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            working = true
            statusStrong = "Reading ZIP..."
            statusText = "Extracting supported audio files."
            scope.launch {
                val r = viewModel.repository.importZip(uri)
                working = false
                statusStrong = "ZIP complete."
                statusText = "${r.added} audio file(s) extracted. (${r.skipped} skipped)"
                viewModel.toast("${r.added} audio file(s) extracted")
            }
        }
    }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            working = true
            statusStrong = "Scanning folder..."
            statusText = "Importing supported audio files."
            scope.launch {
                val r = viewModel.repository.importFolder(uri)
                working = false
                statusStrong = "Folder complete."
                statusText = "${r.added} audio file(s) imported. (${r.skipped} unsupported)"
                viewModel.toast("${r.added} audio file(s) imported")
            }
        }
    }

    val cards = listOf(
        UploadCardUi("Single File", "Import MP3, WAV, M4A, OGG and supported audio files.",
            Icons.Rounded.FilePresent, BrandGreen) { singlePicker.launch(arrayOf("audio/*")) },
        UploadCardUi("ZIP Package", "Extract supported audio files from a ZIP locally.",
            Icons.Rounded.FolderZip, BrandRed) { zipPicker.launch(arrayOf("application/zip", "application/x-zip-compressed")) },
        UploadCardUi("Whole Folder", "Import audio files from a selected folder.",
            Icons.Rounded.FolderOpen, BrandGreen) { folderPicker.launch(null) },
        UploadCardUi("Local Library", "Open audio already stored in this device database.",
            Icons.Rounded.LibraryMusic, BrandGreen) { onOpenLibrary() }
    )

    DashboardScaffold(
        eyebrow = "Import Center",
        titlePrefix = "Add ",
        titleAccent = "Audio",
        accentGreen = false,
        trailing = { CountBadge("${viewModel.audios.value.size} FILES") }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                cards.chunked(2).forEach { rowCards ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowCards.forEach { c ->
                            UploadCard(c, Modifier.weight(1f), enabled = !working)
                        }
                    }
                }
            }

            Spacer(Modifier.height(11.dp))

            GlassBox(modifier = Modifier.fillMaxWidth().padding(13.dp), cornerRadius = 16) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LibraryMusic, null, tint = Color(0xFF59616A), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Local-first — no cloud storage.", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(11.dp))

            GlassBox(modifier = Modifier.fillMaxWidth().padding(13.dp), cornerRadius = 16) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(BrandGreen, RoundedCornerShape(50))
                    )
                    Spacer(Modifier.size(8.dp))
                    Column {
                        Text(statusStrong, color = TextGreenSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.size(3.dp))
                        Text(statusText, color = TextGray, fontSize = 10.sp, lineHeight = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun UploadCard(ui: UploadCardUi, modifier: Modifier = Modifier, enabled: Boolean) {
    val shape = RoundedCornerShape(21.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.02f))))
            .clickable(enabled = enabled) { ui.click() }
            .padding(14.dp)
    ) {
        Icon(ui.icon, null, tint = ui.color, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(12.dp))
        Text(ui.title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text(ui.desc, color = TextGray, fontSize = 9.sp, lineHeight = 13.sp)
    }
}
