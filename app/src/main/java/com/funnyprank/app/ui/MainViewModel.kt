package com.funnyprank.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.funnyprank.app.FunnyPrankApp
import com.funnyprank.app.audio.AudioEngine
import com.funnyprank.app.audio.AudioRouter
import com.funnyprank.app.data.db.AppSettingsEntity
import com.funnyprank.app.data.db.SoundItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = (app as FunnyPrankApp).repository
    val engine = AudioEngine(app)

    private val _sounds = MutableStateFlow<List<SoundItem>>(emptyList())
    val sounds: StateFlow<List<SoundItem>> = _sounds.asStateFlow()

    private val _favorites = MutableStateFlow<List<SoundItem>>(emptyList())
    val favorites: StateFlow<List<SoundItem>> = _favorites.asStateFlow()

    private val _settings = MutableStateFlow(AppSettingsEntity())
    val settings: StateFlow<AppSettingsEntity> = _settings.asStateFlow()

    private val _currentPlayingId = MutableStateFlow<Long?>(null)
    val currentPlayingId: StateFlow<Long?> = _currentPlayingId.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeSounds().collect { list -> _sounds.value = list }
        }
        viewModelScope.launch {
            repository.observeFavorites().collect { list -> _favorites.value = list }
        }
        viewModelScope.launch {
            _settings.value = repository.getSettings() ?: AppSettingsEntity()
        }
    }

    fun toggleFavorite(item: SoundItem) {
        viewModelScope.launch { repository.setFavorite(item.id, !item.isFavorite) }
    }

    fun play(item: SoundItem) {
        engine.stop()
        _currentPlayingId.value = item.id

        val forced = when (_settings.value.outputMode) {
            "SPEAKER" -> AudioRouter.OutputRoute.SPEAKER
            "WIRED" -> AudioRouter.OutputRoute.WIRED
            "BLUETOOTH" -> AudioRouter.OutputRoute.BLUETOOTH
            else -> null
        }
        engine.play(item, forced)
        engine.onCompletion = { _currentPlayingId.value = null }
    }

    fun stop() {
        engine.stop()
        _currentPlayingId.value = null
    }

    fun setOutputMode(mode: String) {
        viewModelScope.launch {
            val updated = repository.getSettings()?.copy(outputMode = mode)
                ?: AppSettingsEntity(outputMode = mode)
            repository.saveSettings(updated)
            _settings.value = updated
        }
    }

    fun markOnboardingDone() {
        viewModelScope.launch {
            val updated = _settings.value.copy(onboardingDone = true)
            repository.saveSettings(updated)
            _settings.value = updated
        }
    }

    fun setOverlayEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val updated = _settings.value.copy(overlayEnabled = enabled)
            repository.saveSettings(updated)
            _settings.value = updated
        }
    }

    fun setVolume(volume: Float) {
        viewModelScope.launch {
            val updated = repository.getSettings()?.copy(volumeBoost = volume)
                ?: AppSettingsEntity(volumeBoost = volume)
            repository.saveSettings(updated)
            _settings.value = updated
        }
        engine.setVolume(volume)
    }

    override fun onCleared() {
        engine.release()
        super.onCleared()
    }
}
