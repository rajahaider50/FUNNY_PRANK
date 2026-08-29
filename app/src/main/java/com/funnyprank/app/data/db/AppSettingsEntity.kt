package com.funnyprank.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val outputMode: String = "AUTO", // AUTO / SPEAKER / WIRED / BLUETOOTH
    val volumeBoost: Float = 1f,
    val overlayEnabled: Boolean = false,
    val onboardingDone: Boolean = false
)
