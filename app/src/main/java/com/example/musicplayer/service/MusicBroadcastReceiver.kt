package com.example.musicplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media.session.MediaButtonReceiver // Import the standard MediaButtonReceiver

class MusicBroadcastReceiver : BroadcastReceiver() {

    // Action constants could be defined here or in MusicService if they are custom
    // For standard actions, MediaSessionCompat constants are used by MediaButtonReceiver

    override fun onReceive(context: Context, intent: Intent) {
        // If this receiver is still specifically targeted by custom intents,
        // forward them to MusicService.
        // For standard media button actions, MediaButtonReceiver.handleIntent is the primary mechanism.
        // However, if notifications are built with PendingIntents targeting this receiver explicitly,
        // this code ensures those intents are passed to the service.

        // The MusicNotification was updated to use MediaButtonReceiver.buildMediaButtonPendingIntent,
        // which will typically target MediaButtonReceiver declared in Manifest, which then calls service's
        // onStartCommand via MediaBrowserServiceCompat or directly if not a media browser service.
        // This receiver might become redundant if all notification actions use MediaButtonReceiver.

        // For safety, if an intent explicitly targets this receiver, we forward it.
        // It's crucial that MusicService#onStartCommand can handle these actions,
        // ideally by delegating to MediaSessionCompat.Callback.

        // The standard MediaButtonReceiver also calls context.startService(serviceIntent).
        // We can replicate a simplified version of forwarding if specific actions are expected here.
        // Or, let MediaButtonReceiver handle it fully.
        // For this conversion, we keep the original forwarding logic to MusicService.

        val serviceIntent = Intent(context, MusicService::class.java)
        serviceIntent.action = intent.action
        // Any extras from the original intent should also be copied if needed
        if (intent.extras != null) {
            serviceIntent.putExtras(intent.extras!!)
        }
        context.startService(serviceIntent)

        // It's also common for MediaButtonReceivers to call MediaButtonReceiver.handleIntent here
        // if this receiver is indeed the one declared for android.intent.action.MEDIA_BUTTON
        // However, the current setup in MusicService's onStartCommand and MusicNotification
        // seems to move towards MediaSession handling.
        // If this BroadcastReceiver is still declared in AndroidManifest for MEDIA_BUTTON action,
        // then it should call:
        // MediaButtonReceiver.handleIntent(MusicService.getMediaSessionCompat(), intent)
        // But this requires MusicService to expose its MediaSession or have a static way to access it,
        // which is not ideal. The service should be the central point of control.
    }
}
