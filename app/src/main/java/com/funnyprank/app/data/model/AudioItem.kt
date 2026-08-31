package com.funnyprank.app.data.model

/**
 * Domain model representing a single audio file managed by the app.
 * Files are copied into app-private storage so the app does not depend
 * on a temporary content URI staying valid.
 */
data class AudioItem(
    val id: Long = 0L,
    val name: String,
    val display: String,
    val size: Long = 0L,
    val mime: String = "audio/*",
    val localPath: String,
    val source: String,
    val created: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L
) {
    val isZeroSized: Boolean get() = size <= 0L
}
