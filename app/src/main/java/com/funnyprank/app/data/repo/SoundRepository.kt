package com.funnyprank.app.data.repo

import com.funnyprank.app.data.db.AppSettingsEntity
import com.funnyprank.app.data.db.SoundDao
import com.funnyprank.app.data.db.SoundItem
import kotlinx.coroutines.flow.Flow

class SoundRepository(private val dao: SoundDao) {

    fun observeSounds(): Flow<List<SoundItem>> = dao.observeSounds()

    fun observeFavorites(): Flow<List<SoundItem>> = dao.observeFavorites()

    suspend fun insert(item: SoundItem) = dao.insert(item)

    suspend fun delete(item: SoundItem) = dao.delete(item)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun getById(id: Long) = dao.getById(id)

    suspend fun getMaxSortOrder(): Int = -1

    suspend fun setFavorite(id: Long, fav: Boolean) = dao.setFavorite(id, fav)

    suspend fun setVolume(id: Long, v: Float) = dao.setVolume(id, v)

    suspend fun setSortOrder(id: Long, order: Int) = dao.setSortOrder(id, order)

    suspend fun getSettings(): AppSettingsEntity? = dao.getSettings()

    suspend fun saveSettings(s: AppSettingsEntity) = dao.upsertSettings(s)
}
