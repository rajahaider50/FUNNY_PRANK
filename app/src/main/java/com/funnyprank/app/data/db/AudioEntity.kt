package com.funnyprank.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio")
data class AudioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val display: String,
    val size: Long = 0L,
    val mime: String = "audio/*",
    val localPath: String,
    val source: String,
    val created: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L
)
