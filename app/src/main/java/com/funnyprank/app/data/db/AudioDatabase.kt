package com.funnyprank.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AudioEntity::class], version = 1, exportSchema = false)
abstract class AudioDatabase : RoomDatabase() {

    abstract fun audioDao(): AudioDao

    companion object {
        @Volatile
        private var instance: AudioDatabase? = null

        fun get(context: Context): AudioDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AudioDatabase::class.java,
                    "funny_prank.db"
                ).build().also { instance = it }
            }
        }
    }
}
