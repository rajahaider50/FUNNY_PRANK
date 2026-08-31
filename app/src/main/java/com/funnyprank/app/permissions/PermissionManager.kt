package com.funnyprank.app.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Maps the four PermissionScreen UI cards to the REAL Android permissions
 * they require, taking Android API-level differences into account.
 */
object PermissionManager {

    enum class Card { MICROPHONE, AUDIO_MEDIA, NEARBY_DEVICES, NOTIFICATIONS }

    /**
     * Android permissions to actually request for a given card
     * (empty means the card needs no runtime permission).
     */
    fun requiredPermissions(card: Card): Array<String> = when (card) {
        Card.MICROPHONE -> arrayOf(Manifest.permission.RECORD_AUDIO)

        // Audio & Media uses the Storage Access Framework picker (no
        // broad storage permission needed) — nothing to request.
        Card.AUDIO_MEDIA -> emptyArray()

        Card.NEARBY_DEVICES ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
            } else {
                // Legacy BLUETOOTH is a normal permission on API <= 30.
                emptyArray()
            }

        Card.NOTIFICATIONS ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                emptyArray()
            }
    }

    /**
     * True when the card's required permissions are already satisfied.
     * Cards that require no runtime permission are always satisfied.
     */
    fun isSatisfied(context: Context, card: Card): Boolean {
        val perms = requiredPermissions(card)
        if (perms.isEmpty()) return true
        return perms.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun allCards(): List<Card> = listOf(
        Card.MICROPHONE, Card.AUDIO_MEDIA, Card.NEARBY_DEVICES, Card.NOTIFICATIONS
    )

    fun countSatisfied(context: Context): Int =
        allCards().count { isSatisfied(context, it) }

    fun allSatisfied(context: Context): Boolean =
        countSatisfied(context) == allCards().size
}
