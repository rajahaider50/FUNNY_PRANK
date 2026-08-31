package com.funnyprank.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.funnyprank.app.AppContainer
import com.funnyprank.app.data.AudioRepository
import com.funnyprank.app.data.SettingsRepository
import com.funnyprank.app.data.model.AudioItem
import com.funnyprank.app.player.PlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val container = AppContainer.get(application)
    val repository: AudioRepository = container.audioRepository
    val settings: SettingsRepository = container.settings

    val audios: StateFlow<List<AudioItem>> =
        repository.observeAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val playback: StateFlow<PlaybackState> = container.audioPlayer.state

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _activeTab = MutableStateFlow(DashboardTab.HOME)
    val activeTab: StateFlow<DashboardTab> = _activeTab

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun setTab(tab: DashboardTab) { _activeTab.value = tab }

    fun setSearch(q: String) { _searchQuery.value = q }

    fun filteredAudios(): List<AudioItem> {
        val q = _searchQuery.value.trim().lowercase()
        val list = audios.value
        if (q.isEmpty()) return list
        return list.filter {
            it.display.lowercase().contains(q) || it.name.lowercase().contains(q)
        }
    }

    fun playPreview(item: AudioItem) {
        if (!settings.audioPreview) {
            toast("Audio preview is disabled in Settings")
            return
        }
        container.audioPlayer.playItem(item)
        com.funnyprank.app.service.PlaybackService.start(getApplication<Application>())
    }

    fun toggleCurrent() {
        if (playback.value.current == null) return
        container.audioPlayer.togglePlay()
    }

    fun stopPlayback() {
        container.audioPlayer.stop()
        com.funnyprank.app.service.PlaybackService.stop(getApplication<Application>())
    }

    fun currentPositionMs(): Long = container.audioPlayer.currentPositionMs()

    fun rename(id: Long, newName: String) {
        viewModelScope.launch {
            val ok = repository.rename(id, newName)
            toast(if (ok) "Audio renamed" else "Could not rename")
        }
    }

    fun delete(item: AudioItem) {
        viewModelScope.launch {
            val ok = repository.delete(item.id)
            toast(if (ok) "Audio deleted" else "Could not delete")
        }
    }

    fun setAudioPreview(enabled: Boolean) {
        settings.audioPreview = enabled
        toast(if (enabled) "Audio preview enabled" else "Audio preview disabled")
    }

    fun setDarkTheme(enabled: Boolean) {
        settings.darkTheme = enabled
        toast(if (enabled) "Dark Glass theme enabled" else "Theme preference changed")
    }

    fun setFloatingControl(enabled: Boolean) {
        settings.floatingControl = enabled
        toast(if (enabled) "Floating control enabled" else "Floating control disabled")
    }

    fun toast(message: String) {
        _message.value = message
    }

    fun consumeMessage() {
        _message.value = null
    }

    enum class DashboardTab { HOME, AUDIO, UPLOAD, EDIT, SETTINGS }
}
