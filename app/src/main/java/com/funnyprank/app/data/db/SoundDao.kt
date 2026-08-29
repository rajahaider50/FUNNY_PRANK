package com.funnyprank.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundDao {

    @Query("SELECT * FROM sounds ORDER BY sortOrder ASC, createdAt DESC")
    fun observeSounds(): Flow<List<SoundItem>>

    @Query("SELECT * FROM sounds WHERE isFavorite = 1 ORDER BY sortOrder ASC")
    fun observeFavorites(): Flow<List<SoundItem>>

    @Query("SELECT * FROM sounds WHERE id = :id")
    suspend fun getById(id: Long): SoundItem?

    @Insert
    suspend fun insert(item: SoundItem): Long

    @Update
    suspend fun update(item: SoundItem)

    @Delete
    suspend fun delete(item: SoundItem)

    @Query("UPDATE sounds SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: Long, fav: Boolean)

    @Query("UPDATE sounds SET volume = :vol WHERE id = :id")
    suspend fun setVolume(id: Long, vol: Float)

    @Query("UPDATE sounds SET sortOrder = :order WHERE id = :id")
    suspend fun setSortOrder(id: Long, order: Int)

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(s: AppSettingsEntity)

    @Query("DELETE FROM sounds WHERE id = :id")
    suspend fun deleteById(id: Long)
}
