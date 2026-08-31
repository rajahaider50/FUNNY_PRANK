package com.funnyprank.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {

    @Query("SELECT * FROM audio ORDER BY created DESC")
    fun observeAll(): Flow<List<AudioEntity>>

    @Query("SELECT * FROM audio ORDER BY created DESC")
    suspend fun getAll(): List<AudioEntity>

    @Query("SELECT * FROM audio WHERE id = :id")
    suspend fun getById(id: Long): AudioEntity?

    @Query(
        "SELECT COUNT(*) FROM audio WHERE name = :name AND size = :size"
    )
    suspend fun countByUnique(name: String, size: Long): Int

    @Insert
    suspend fun insert(entity: AudioEntity): Long

    @Update
    suspend fun update(entity: AudioEntity)

    @Delete
    suspend fun delete(entity: AudioEntity)

    @Query("DELETE FROM audio WHERE id = :id")
    suspend fun deleteById(id: Long)
}
