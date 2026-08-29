package com.funnyprank.app.import

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import com.funnyprank.app.FunnyPrankApp
import com.funnyprank.app.data.db.SoundItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipInputStream

/**
 * Handles importing sounds into app-specific storage and registering them in
 * Room. Formats supported: mp3, wav, ogg, m4a, aac, 3gp, amr.
 */
object SoundImporter {

    private val AUDIO_EXT = setOf("mp3", "wav", "ogg", "m4a", "aac", "3gp", "amr")

    fun isAudio(name: String): Boolean =
        name.substringAfterLast('.', "").lowercase() in AUDIO_EXT

    private fun audioDir(context: Context): File =
        File(context.filesDir, "sounds").apply { if (!exists()) mkdirs() }

    suspend fun importUris(context: Context, uris: List<Uri>): Int = withContext(Dispatchers.IO) {
        var count = 0
        val app = context.applicationContext as FunnyPrankApp
        for (uri in uris) {
            val name = queryName(context, uri) ?: "audio_${System.currentTimeMillis()}"
            if (!isAudio(name)) continue
            val dest = File(audioDir(context), "imp_${System.currentTimeMillis()}_${File(name).name}")
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    dest.outputStream().use { out -> input.copyTo(out) }
                }
                val dur = probeDuration(context, uri)
                app.repository.insert(
                    SoundItem(
                        displayName = File(name).name.substringBeforeLast('.'),
                        fileName = File(name).name,
                        localPath = dest.absolutePath,
                        durationMs = dur,
                        sortOrder = System.currentTimeMillis().toInt()
                    )
                )
                count++
            }
        }
        count
    }

    suspend fun importTree(context: Context, treeUri: Uri): Int = withContext(Dispatchers.IO) {
        var count = 0
        val app = context.applicationContext as FunnyPrankApp
        val connection = DocumentFile.fromTreeUri(context, treeUri) ?: return@withContext 0

        suspend fun walk(doc: DocumentFile, prefix: String) {
            doc.listFiles().forEach { child ->
                val name = child.name ?: return@forEach
                if (child.isDirectory) {
                    walk(child, "${prefix}_")
                } else if (isAudio(name)) {
                    runCatching {
                        val dest = File(audioDir(context), "folder${prefix}_${File(name).name}")
                        context.contentResolver.openInputStream(child.uri)?.use { input ->
                            dest.outputStream().use { out -> input.copyTo(out) }
                        }
                        val dur = probeDuration(context, child.uri)
                        app.repository.insert(
                            SoundItem(
                                displayName = name.substringBeforeLast('.'),
                                fileName = name,
                                localPath = dest.absolutePath,
                                durationMs = dur,
                                sortOrder = System.currentTimeMillis().toInt()
                            )
                        )
                        count++
                    }
                }
            }
        }
        walk(connection, "")
        count
    }

    suspend fun importZip(context: Context, zipUri: Uri): Int = withContext(Dispatchers.IO) {
        var count = 0
        val app = context.applicationContext as FunnyPrankApp
        runCatching {
            context.contentResolver.openInputStream(zipUri)?.use { input ->
                ZipInputStream(input).use { zis ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        val name = entry.name
                        if (!entry.isDirectory && isAudio(name)) {
                            val flat = name.substringAfterLast('/')
                            val dest = File(audioDir(context), "zip_${System.nanoTime()}_${File(flat).name}")
                            dest.outputStream().use { out -> zis.copyTo(out) }
                            app.repository.insert(
                                SoundItem(
                                    displayName = flat.substringBeforeLast('.'),
                                    fileName = flat,
                                    localPath = dest.absolutePath,
                                    durationMs = 0,
                                    sortOrder = System.currentTimeMillis().toInt()
                                )
                            )
                            count++
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            }
        }
        count
    }

    private fun queryName(context: Context, uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { c ->
                if (c.moveToFirst()) {
                    val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) c.getString(idx) else null
                } else null
            }
        }.getOrNull()
    }

    // Uses MediaMetadataRetriever to get duration without starting playback.
    private fun probeDuration(context: Context, uri: Uri): Long {
        return runCatching {
            val mmr = android.media.MediaMetadataRetriever()
            mmr.setDataSource(context, uri)
            val d = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            mmr.release()
            d
        }.getOrDefault(0L)
    }
}
