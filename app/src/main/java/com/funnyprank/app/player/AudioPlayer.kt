package com.funnyprank.app.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.funnyprank.app.data.model.AudioItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class PlaybackState(
    val current: AudioItem? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L
)

@OptIn(UnstableApi::class)
class AudioPlayer(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state

    private var exoPlayer: ExoPlayer? = null

    private val focusRequest: AudioFocusRequest? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener { change ->
                    when (change) {
                        AudioManager.AUDIOFOCUS_LOSS -> pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                            exoPlayer?.volume = 0.2f
                        }
                        AudioManager.AUDIOFOCUS_GAIN -> {
                            exoPlayer?.volume = 1f
                        }
                    }
                }
                .build()
        } else null
    }

    private fun ensurePlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context).build().also { player ->
            exoPlayer = player
            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _state.update { it.copy(isPlaying = isPlaying) }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            val d = player.duration
                            _state.update {
                                it.copy(durationMs = if (d > 0) d else it.durationMs)
                            }
                        }
                        Player.STATE_ENDED -> {
                            _state.update { it.copy(isPlaying = false, positionMs = 0L) }
                        }
                    }
                }
            })
        }
    }

    /** Current playback position in ms (used to drive the progress bar). */
    fun currentPositionMs(): Long = try {
        exoPlayer?.currentPosition ?: 0L
    } catch (_: Exception) {
        0L
    }

    fun playItem(item: AudioItem) {
        val player = ensurePlayer()
        val url = java.io.File(item.localPath).absoluteFile.toURI().toString()
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        _state.update {
            it.copy(current = item, isPlaying = true, positionMs = 0L, durationMs = item.durationMs)
        }
        requestFocus()
        player.play()
    }

    fun play() {
        requestFocus()
        try {
            ensurePlayer().play()
        } catch (_: Exception) {
        }
    }

    fun pause() {
        try {
            exoPlayer?.pause()
        } catch (_: Exception) {
        }
    }

    fun togglePlay() {
        if (_state.value.isPlaying) pause() else play()
    }

    fun stop() {
        try {
            exoPlayer?.stop()
        } catch (_: Exception) {
        }
        _state.update { PlaybackState(current = it.current, isPlaying = false, positionMs = 0L, durationMs = it.durationMs) }
        abandonFocus()
    }

    fun release() {
        try {
            exoPlayer?.release()
        } catch (_: Exception) {
        }
        exoPlayer = null
        abandonFocus()
    }

    private fun requestFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.requestAudioFocus(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    private fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }
}
