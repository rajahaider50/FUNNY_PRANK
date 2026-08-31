package com.funnyprank.app.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.funnyprank.app.data.db.AudioDao
import com.funnyprank.app.data.db.AudioEntity
import com.funnyprank.app.data.model.AudioItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class AudioRepository(
    private val context: Context,
    private val dao: AudioDao
) {

    data class ImportResult(val added: Int, val duplicates: Int, val skipped: Int, val messages: List<String> = emptyList())

    companion object {
        val SUPPORTED_EXTENSIONS = setOf("mp3", "wav", "m4a", "aac", "ogg", "oga", "opus", "flac", "webm", "3gp")

        fun isSupportedAudio(name: String): Boolean {
            val ext = name.substringAfterLast('.', "").lowercase()
            return ext in SUPPORTED_EXTENSIONS
        }

        fun displayNameFrom(name: String): String = name.substringAfterLast('/').substringBeforeLast('.')

        fun mimeForName(name: String): String {
            val ext = name.substringAfterLast('.', "").lowercase()
            val map = mapOf(
                "mp3" to "audio/mpeg", "wav" to "audio/wav", "m4a" to "audio/mp4",
                "aac" to "audio/aac", "ogg" to "audio/ogg", "oga" to "audio/ogg",
                "opus" to "audio/opus", "flac" to "audio/flac", "webm" to "audio/webm",
                "3gp" to "audio/3gpp"
            )
            return map[ext] ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "audio/*"
        }
    }

    fun observeAll(): Flow<List<AudioItem>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    suspend fun all(): List<AudioItem> = withContext(Dispatchers.IO) { dao.getAll().map { it.toModel() } }

    private fun audioDir(): File =
        File(context.filesDir, "audio").apply { mkdirs() }

    // ---------------------------------------------------------------
    //  Dedupe + persistence
    // ---------------------------------------------------------------
    private suspend fun alreadyExists(name: String, size: Long): Boolean =
        dao.countByUnique(name, size) > 0

    private suspend fun persist(name: String, display: String, size: Long, mime: String, localPath: String, source: String): Long =
        dao.insert(
            AudioEntity(
                name = name, display = display, size = size, mime = mime,
                localPath = localPath, source = source, created = System.currentTimeMillis()
            )
        )

    // ---------------------------------------------------------------
    //  Copy a content Uri into private storage
    // ---------------------------------------------------------------
    private suspend fun copyUriToStorage(
        uri: Uri,
        fallbackName: String,
        source: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver
                val meta = resolver.query(uri, null, null, null, null)?.use { c ->
                    if (c.moveToFirst()) {
                        val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIdx = c.getColumnIndex(OpenableColumns.SIZE)
                        Triple(
                            if (nameIdx >= 0) c.getString(nameIdx) else fallbackName,
                            if (sizeIdx >= 0 && !c.isNull(sizeIdx)) c.getLong(sizeIdx) else -1L,
                            c.getType(0) ?: ""
                        )
                    } else null
                }

                val name = meta?.first ?: fallbackName
                if (!isSupportedAudio(name)) return@withContext false

                val size = meta?.second ?: -1L

                // optional persistence: if size matches existing, skip
                if (size >= 0 && alreadyExists(name, size)) return@withContext false

                val destFile = uniquify(File(audioDir(), sanitize(name)))
                val input: InputStream? = resolver.openInputStream(uri)
                if (input == null) return@withContext false

                input.use { ins ->
                    FileOutputStream(destFile).use { out -> ins.copyTo(out) }
                }

                val writtenSize = destFile.length()
                persist(name, displayNameFrom(name), writtenSize, mimeForName(name), destFile.absolutePath, source)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    // ---------------------------------------------------------------
    //  Single file / multiple files (SAF picker)
    // ---------------------------------------------------------------
    suspend fun importSingle(uri: Uri, source: String = "Single file"): ImportResult {
        val added = if (copyUriToStorage(uri, uri.lastPathSegment ?: "audio", source)) 1 else 0
        return ImportResult(added = added, duplicates = if (added == 0) 1 else 0, skipped = 0)
    }

    suspend fun importMultiple(uris: List<Uri>, source: String = "Single file"): ImportResult {
        var added = 0; var dup = 0; var skipped = 0
        for (uri in uris) {
            val ok = copyUriToStorage(uri, uri.lastPathSegment ?: "audio", source)
            when {
                ok -> added++
                else -> { /* unsupported or duplicate — treat as duplicate */ dup++ }
            }
        }
        return ImportResult(added, dup, skipped)
    }

    // ---------------------------------------------------------------
    //  Folder (SAF directory picker) — enumerate supported children
    // ---------------------------------------------------------------
    suspend fun importFolder(uri: Uri): ImportResult = withContext(Dispatchers.IO) {
        val children = context.contentResolver
            .query(uri, null, null, null, null)
            ?.use { c ->
                val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                buildList {
                    while (c.moveToNext()) {
                        if (nameIdx >= 0 && !c.isNull(nameIdx)) add(c.getString(nameIdx))
                    }
                }
            } ?: emptyList()

        var added = 0; var dup = 0; var skipped = 0
        for (name in children) {
            if (!isSupportedAudio(name)) { skipped++; continue }
            val childUri = Uri.withAppendedPath(uri, name)
            // fallback name per child
            val ok = copyUriToStorage(childUri, name, "Folder")
            if (ok) added++ else dup++
        }
        ImportResult(added, dup, skipped)
    }

    // ---------------------------------------------------------------
    //  ZIP extraction with path-traversal protection
    // ---------------------------------------------------------------
    suspend fun importZip(uri: Uri): ImportResult = withContext(Dispatchers.IO) {
        val added = mutableListOf<String>()
        val dups = mutableListOf<String>()
        val invalid = mutableListOf<String>()
        val resolver = context.contentResolver
        val input = resolver.openInputStream(uri) ?: return@withContext ImportResult(0, 0, 0, listOf("Cannot open ZIP"))

        val root = audioDir()
        val MAX_TOTAL = 200 * 1024 * 1024L
        var total = 0L

        try {
            ZipInputStream(input.buffered()).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    val name = entry.name
                    if (!entry.isDirectory && isSupportedAudio(name)) {
                        val safeName = name.substringAfterLast('/')
                        if (safeName.isEmpty()) {
                            invalid.add(name)
                        } else {
                            val dest = uniquify(File(root, sanitize(safeName)))
                                            val out = FileOutputStream(dest)
                            var count = 0L
                            try {
                                zis.copyTo(out)
                                count = dest.length()
                            } finally {
                                out.close()
                            }
                            total += count
                            if (total > MAX_TOTAL) {
                                dest.delete()
                                invalid.add(name)
                            } else {
                                persist(name, displayNameFrom(safeName), count, mimeForName(safeName), dest.absolutePath, "ZIP package")
                                added.add(name)
                            }
                        }
                    } else {
                        invalid.add(name)
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } catch (e: Exception) {
            return@withContext ImportResult(0, 0, 0, listOf("Failed to read ZIP: ${e.message}"))
        }

        ImportResult(added.size, dups.size, invalid.size, listOf("${added.size} imported"))
    }

    // ---------------------------------------------------------------
    //  Rename / Delete
    // ---------------------------------------------------------------
    suspend fun rename(id: Long, newDisplay: String): Boolean = withContext(Dispatchers.IO) {
        val entity = dao.getById(id) ?: return@withContext false
        val cleaned = newDisplay.trim().take(120)
        if (cleaned.isEmpty()) return@withContext false
        dao.update(entity.copy(display = cleaned))
        true
    }

    suspend fun delete(id: Long): Boolean = withContext(Dispatchers.IO) {
        val entity = dao.getById(id) ?: return@withContext false
        try {
            File(entity.localPath).delete()
        } catch (_: Exception) {
        }
        dao.delete(entity)
        true
    }

    // ---------------------------------------------------------------
    //  Helpers
    // ---------------------------------------------------------------
    private fun sanitize(name: String): String {
        val base = name.replace(Regex("[\\/:*?\"<>|]"), "_")
        return base.ifBlank { "audio" }
    }

    private fun uniquify(file: File): File {
        if (!file.exists()) return file
        val prefix = file.nameWithoutExtension
        val ext = file.extension
        var counter = 1
        var candidate: File
        do {
            candidate = File(file.parentFile, "${prefix}_$counter.${ext.ifEmpty { "mp3" }}")
            counter++
        } while (candidate.exists())
        return candidate
    }

    private fun AudioEntity.toModel() = AudioItem(
        id = id, name = name, display = display, size = size, mime = mime,
        localPath = localPath, source = source, created = created, durationMs = durationMs
    )
}
