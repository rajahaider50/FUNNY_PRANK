package com.funnyprank.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.io.File
import java.io.FileInputStream
import kotlin.concurrent.thread

/**
 * Low-level PCM playback engine built on AudioTrack.
 *
 * Architecture (expandable for future mixing):
 *
 *   Audio Decoder / PCM Source  ->  [ PcmBuffer  ->  AudioMixer  ->  Volume  ]  ->  AudioTrack  ->  OUTPUT
 *
 * Future "Mic PCM + Funny PCM -> Mixer -> Output" only needs to feed a second
 * PCM source into [AudioMixer] (see PcmMixer). This engine already owns the
 * output stage so nothing in the routing layer needs to change.
 *
 * For WAV (PCM) we decode directly. For compressed formats (MP3/OGG/M4A) the
 * primary path uses Media3/ExoPlayer (PlaybackService); this engine is the
 * low-level / future-processing path.
 *
 * NOTE: WAV decoding here is 16-bit PCM stereo/mono. This is intentionally
 * lightweight and does not attempt full compressed codec decode in Kotlin.
 */
class PcmAudioEngine {

    private var track: AudioTrack? = null
    private var playingThread: Thread? = null
    @Volatile private var playing = false

    var onComplete: (() -> Unit)? = null

    companion object {
        const val SAMPLE_RATE = 44100

        fun toAudioUsage(bluetoothMono: Boolean): Int =
            if (bluetoothMono) AudioAttributes.USAGE_VOICE_COMMUNICATION
            else AudioAttributes.USAGE_MEDIA
    }

    /**
     * Plays a 16-bit PCM WAV file through AudioTrack.
     * @param monoForBluetooth when true forces mono + voice-comm usage so the
     *        track is compatible with Bluetooth SCO routing.
     */
    fun playWav(file: File, volume: Float = 1f, monoForBluetooth: Boolean = false) {
        stop()
        val wav = readWavHeader(file) ?: return
        val track = buildTrack(wav, monoForBluetooth)
        this.track = track
        playing = true

        playingThread = thread(name = "pcm-playback") {
            try {
                track.play()
                val bytes = ByteArray(8192)
                FileInputStream(file).use { fis ->
                    fis.skip(wav.dataOffset.toLong())
                    while (playing) {
                        val read = fis.read(bytes)
                        if (read <= 0) break
                        val out = if (volume != 1f) scale(bytes, read, volume) else bytes
                        // Note: blocking write keeps timing; pause handled via stop().
                        track.write(out, 0, read)
                    }
                }
                onComplete?.invoke()
            } catch (_: Exception) {
            } finally {
                release()
            }
        }
    }

    private data class WavInfo(
        val channels: Int,
        val bitsPerSample: Int,
        val sampleRate: Int,
        val dataOffset: Int,
        val byteRate: Int
    )

    private fun readWavHeader(file: File): WavInfo? {
        return try {
            FileInputStream(file).use { fis ->
                val h = ByteArray(44)
                if (fis.read(h) != 44) return null
                if (!h.copyOfRange(0, 4).contentEquals(byteArrayOf('R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte()))) {
                    return null
                }
                if (!h.copyOfRange(8, 12).contentEquals(byteArrayOf('W'.code.toByte(), 'A'.code.toByte(), 'V'.code.toByte(), 'E'.code.toByte()))) {
                    return null
                }
                val channels = ((h[22].toInt() and 0xff) or ((h[23].toInt() and 0xff) shl 8))
                val sampleRate = ((h[24].toInt() and 0xff) or ((h[25].toInt() and 0xff) shl 8) or
                    ((h[26].toInt() and 0xff) shl 16) or ((h[27].toInt() and 0xff) shl 24))
                val bits = ((h[34].toInt() and 0xff) or ((h[35].toInt() and 0xff) shl 8))
                val byteRate = ((h[28].toInt() and 0xff) or ((h[29].toInt() and 0xff) shl 8) or
                    ((h[30].toInt() and 0xff) shl 16) or ((h[31].toInt() and 0xff) shl 24))
                var offset = 12
                var foundData = -1
                var dataIndex = -1
                while (offset + 8 <= h.size) {
                    val id = String(h, offset, 4)
                    val size = ((h[offset + 4].toInt() and 0xff) or ((h[offset + 5].toInt() and 0xff) shl 8) or
                        ((h[offset + 6].toInt() and 0xff) shl 16) or ((h[offset + 7].toInt() and 0xff) shl 24))
                    if (id == "data") {
                        foundData = size
                        dataIndex = offset + 8
                        break
                    }
                    offset += 8 + size + (size % 2)
                }
                if (dataIndex < 0) return null
                WavInfo(channels, bits, sampleRate, dataIndex, byteRate)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun buildTrack(wav: WavInfo, monoForBluetooth: Boolean): AudioTrack {
        val channelOut = if (wav.channels == 2 && !monoForBluetooth)
            AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_OUT_MONO
        val format = AudioFormat.Builder()
            .setSampleRate(wav.sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(channelOut)
            .build()
        val attrs = AudioAttributes.Builder()
            .setUsage(toAudioUsage(monoForBluetooth))
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        val minBuf = AudioTrack.getMinBufferSize(wav.sampleRate, channelOut, AudioFormat.ENCODING_PCM_16BIT)
        val buffer = maxOf(minBuf, 8192)
        return AudioTrack.Builder()
            .setAudioAttributes(attrs)
            .setAudioFormat(format)
            .setBufferSizeInBytes(buffer)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    private fun scale(bytes: ByteArray, len: Int, volume: Float): ByteArray {
        if (volume == 1f) return bytes
        val out = ByteArray(len)
        var i = 0
        while (i + 1 < len) {
            var s = ((bytes[i].toInt() and 0xff) or (bytes[i + 1].toInt() shl 8)).toShort()
            s = (s * volume).toInt().toShort()
            out[i] = (s.toInt() and 0xff).toByte()
            out[i + 1] = (s.toInt() shr 8).toByte()
            i += 2
        }
        return out
    }

    val isPlaying: Boolean get() = playing

    fun stop() {
        playing = false
        playingThread?.interrupt()
        release()
    }

    private fun release() {
        runCatching { track?.stop() }
        track?.release()
        track = null
        playing = false
    }
}

/**
 * Future mixing stage: a trivial linear PCM mixer. Feed multiple sources
 * (e.g. mic + funny audio) as float/16-bit PCM and it sums + clips them.
 */
object PcmMixer {
    /**
     * Mixes two 16-bit mono PCM streams sample-by-sample with per-source gain.
     * Returns a new array of the same length (shortCount * 2 bytes).
     */
    fun mix(a: ShortArray, b: ShortArray, gainA: Float, gainB: Float): ShortArray {
        val n = minOf(a.size, b.size)
        val out = ShortArray(n)
        for (i in 0 until n) {
            val v = (a[i] * gainA + b[i] * gainB).toInt()
            out[i] = v.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return out
    }
}
