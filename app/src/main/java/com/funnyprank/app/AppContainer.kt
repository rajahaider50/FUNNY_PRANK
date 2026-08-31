package com.funnyprank.app

import android.content.Context
import com.funnyprank.app.data.AudioRepository
import com.funnyprank.app.data.SettingsRepository
import com.funnyprank.app.data.db.AudioDatabase
import com.funnyprank.app.player.AudioPlayer

/**
 * Simple manual service locator shared across the Activity and Services.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database: AudioDatabase by lazy { AudioDatabase.get(appContext) }

    val settings: SettingsRepository by lazy { SettingsRepository(appContext) }
    val audioRepository: AudioRepository by lazy { AudioRepository(appContext, database.audioDao()) }
    val audioPlayer: AudioPlayer by lazy { AudioPlayer(appContext) }

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun get(context: Context): AppContainer {
            return instance ?: synchronized(this) {
                instance ?: AppContainer(context.applicationContext).also { instance = it }
            }
        }
    }
}
