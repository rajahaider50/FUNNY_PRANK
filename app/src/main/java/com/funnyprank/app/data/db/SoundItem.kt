package com.funnyprank.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sounds")
data class SoundItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val displayName: String,
    val fileName: String,
    val localPath: String,
    val durationMs: Long = 0,
    val category: String = "General",
    val isFavorite: Boolean = false,
    val volume: Float = 1f,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
