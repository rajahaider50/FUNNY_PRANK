package com.funnyprank.app.audio

import android.content.Context
import com.funnyprank.app.data.db.SoundItem
import java.io.File

/**
 * Single facade over the playback stack.
 *
 * Circuit:
 *   SoundItem -> File -> MediaPlaybackManager (ExoPlayer for compressed)
 *                       -> PcmAudioEngine (low-level WAV / future mixing)
 *   route = AudioRouter.currentRoute (speaker / wired / bluetooth)
 *
 * Keeps one active playback at a time (tap a sound, previous stops).
 */
class AudioEngine(context: Context) {

    private val appContext = context.applicationContext
    private val router = AudioRouter(appContext)
    private val mediaManager = MediaPlaybackManager(appContext)
    private val pcmEngine = PcmAudioEngine()

    var onCompletion: (() -> Unit)? = null

    private var volumeMultiplier = 1f

    fun setVolume(volume: Float) {
        volumeMultiplier = volume.coerceIn(0f, 1f)
    }

    val currentRoute: String
        get() = when (router.currentRoute.value) {
            AudioRouter.OutputRoute.SPEAKER -> "Speaker"
            AudioRouter.OutputRoute.WIRED -> "Wired"
            AudioRouter.OutputRoute.BLUETOOTH -> "Bluetooth"
        }

    val isPlaying: Boolean
        get() = mediaManager.isPlaying || pcmEngine.isPlaying

    /**
     * Plays a [SoundItem]. Prefers ExoPlayer for compressed formats and falls
     * back to the raw PCM engine for WAV (keeping the processing pipeline
     * available for future use).
     */
    fun play(item: SoundItem, manualRoute: AudioRouter.OutputRoute? = null) {
        val file = File(item.localPath)
        if (!file.exists()) return

        val route = manualRoute ?: router.currentRoute.value
        router.applyRoute(route)
        val monoForBt = route == AudioRouter.OutputRoute.BLUETOOTH

        stop()

        val effectiveVolume = (item.volume * volumeMultiplier).coerceIn(0f, 1f)

        if (item.localPath.substringAfterLast('.').lowercase() == "wav") {
            pcmEngine.playWav(file, effectiveVolume, monoForBt)
            pcmEngine.onComplete = { onCompletion?.invoke() }
        } else {
            mediaManager.play(file, effectiveVolume, monoForBt) { onCompletion?.invoke() }
        }
    }

    fun stop() {
        mediaManager.stop()
        pcmEngine.stop()
    }

    fun release() {
        stop()
        router.release()
    }
}
