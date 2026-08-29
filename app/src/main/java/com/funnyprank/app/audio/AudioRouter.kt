package com.funnyprank.app.audio

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Determines and reacts to the currently active audio output route.
 *
 * Supported routes:
 *  - SPEAKER   : nothing wired/Bluetooth -> sound via phone speaker
 *  - WIRED     : 3.5mm / USB audio device connected
 *  - BLUETOOTH : A2DP / SCO headset connected
 *
 * The router is device-listener based so it reacts to plug/unplug while the
 * app is running, letting the engine re-route safely.
 */
class AudioRouter(context: Context) {

    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    enum class OutputRoute { SPEAKER, WIRED, BLUETOOTH }

    private val _currentRoute = MutableStateFlow(detectCurrentRoute())
    val currentRoute: StateFlow<OutputRoute> = _currentRoute.asStateFlow()

    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
            _currentRoute.value = detectCurrentRoute()
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
            _currentRoute.value = detectCurrentRoute()
        }
    }

    init {
        audioManager.registerAudioDeviceCallback(deviceCallback, null)
    }

    fun detectCurrentRoute(): OutputRoute {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val hasWired = devices.any {
            it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                it.type == AudioDeviceInfo.TYPE_USB_HEADSET ||
                it.type == AudioDeviceInfo.TYPE_USB_DEVICE ||
                it.type == AudioDeviceInfo.TYPE_LINE_ANALOG
        }
        val hasBluetooth = devices.any {
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
        }
        return when {
            hasBluetooth -> OutputRoute.BLUETOOTH
            hasWired -> OutputRoute.WIRED
            else -> OutputRoute.SPEAKER
        }
    }

    /**
     * Applies output routing preferences for the current [AudioManager].
     * Uses modern per-device selection where available (API 31+), with a
     * graceful pre-31 fallback via MODE_IN_COMMUNICATION + SCO for Bluetooth.
     */
    fun applyRoute(route: OutputRoute) {
        try {
            if (route == OutputRoute.BLUETOOTH) {
                // Push audio into the in-call/communication stream so a
                // Bluetooth SCO headset (mic+speaker combo) routes it.
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                audioManager.isBluetoothScoOn = true
                if (!audioManager.isBluetoothScoOn) {
                    @Suppress("DEPRECATION")
                    audioManager.startBluetoothSco()
                }
                audioManager.isSpeakerphoneOn = false
            } else {
                audioManager.isBluetoothScoOn = false
                @Suppress("DEPRECATION")
                audioManager.stopBluetoothSco()
                audioManager.mode = AudioManager.MODE_NORMAL
                audioManager.isSpeakerphoneOn = (route == OutputRoute.SPEAKER)
            }
        } catch (_: SecurityException) {
            // BLUETOOTH_CONNECT permission not yet granted; keep going.
        }
    }

    fun release() {
        runCatching { audioManager.unregisterAudioDeviceCallback(deviceCallback) }
    }
}
