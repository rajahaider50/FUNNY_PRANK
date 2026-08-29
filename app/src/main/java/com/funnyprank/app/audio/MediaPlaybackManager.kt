package com.funnyprank.app.audio

import android.content.Context
import android.media.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.io.File

/**
 * High-level playback using Media3 (ExoPlayer) for compressed codecs
 * (MP3/OGG/M4A/AAC). Combined with [AudioRouter] it routes output to the
 * active device (speaker / wired / Bluetooth).
 *
 * The [PcmAudioEngine] remains the future low-level / processing path; for
 * everyday playing of imported sounds we prefer ExoPlayer (robust codecs).
 */
class MediaPlaybackManager(context: Context) {

    private val appContext = context.applicationContext
    private var exo: ExoPlayer? = null
    private var _isPlaying = false
    var onCompletion: (() -> Unit)? = null

    val isPlaying: Boolean get() = _isPlaying

    fun play(file: File, volume: Float, bluetoothMono: Boolean, onEnd: (() -> Unit)? = null) {
        onCompletion = onEnd
        stopInternal()

        val usage = AudioAttributes.USAGE_VOICE_COMMUNICATION
        val contentType = AudioAttributes.CONTENT_TYPE_SPEECH

        exo = runCatching {
            ExoPlayer.Builder(appContext).build().also { player ->
                val aa = androidx.media3.common.AudioAttributes.Builder()
                    .setUsage(usage)
                    .setContentType(contentType)
                    .build()
                player.setAudioAttributes(aa, true)
                player.volume = volume
                player.setMediaItem(MediaItem.fromUri(android.net.Uri.fromFile(file)))
                player.prepare()
                player.playWhenReady = true
            }
        }.getOrNull()

        exo?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_ENDED -> {
                        _isPlaying = false
                        onCompletion?.invoke()
                    }
                    Player.STATE_READY -> _isPlaying = true
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                _isPlaying = false
                stopInternal()
            }
        })
    }

    fun stop() = stopInternal()

    private fun stopInternal() {
        exo?.release()
        exo = null
        _isPlaying = false
    }
}
