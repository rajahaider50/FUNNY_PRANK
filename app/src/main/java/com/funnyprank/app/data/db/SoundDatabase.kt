package com.funnyprank.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SoundItem::class, AppSettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SoundDatabase : RoomDatabase() {

    abstract fun soundDao(): SoundDao

    companion object {
        @Volatile
        private var INSTANCE: SoundDatabase? = null

        fun getInstance(context: Context): SoundDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SoundDatabase::class.java,
                    "funny_prank.db"
                ).build().also { INSTANCE = it }
            }
    }
}
