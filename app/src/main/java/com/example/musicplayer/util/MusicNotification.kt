package com.example.musicplayer.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver // Make sure this import is correct for your setup
import com.example.musicplayer.R
import com.example.musicplayer.controller.activity.MusicSingleTrackActivity // Assuming this is the correct activity
import com.example.musicplayer.model.Song
import com.example.musicplayer.service.MusicService

object MusicNotification {

    private const val REQUEST_CODE_NOTIFICATION = 1
    const val NOTIFICATION_ID = 1 // Changed from 0 to avoid potential conflicts
    const val CHANNEL_ID = "music_player_channel" // Define a channel ID

    fun createNotification(
        context: Context,
        mediaSessionToken: MediaSessionCompat.Token,
        song: Song,
        isPlaying: Boolean
    ): Notification {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "Music Player Controls",
                android.app.NotificationManager.IMPORTANCE_LOW // Use LOW to avoid sound, can be MIN for no visual interruption
            )
            channel.description = "Notifications for music playback control"
            channel.setSound(null, null) // No sound for media notifications usually
            channel.enableLights(false)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_NOTIFICATION,
            MusicSingleTrackActivity.newIntent(context), // Intent to open when notification is clicked
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val mediaMetadataRetriever = MediaMetadataRetriever()
        var songImage: Bitmap? = null
        try {
            mediaMetadataRetriever.setDataSource(song.path)
            val pic = mediaMetadataRetriever.embeddedPicture
            pic?.let {
                songImage = BitmapFactory.decodeByteArray(it, 0, it.size)
            }
        } catch (e: Exception) {
            // Handle error if artwork can't be loaded
            e.printStackTrace()
        } finally {
            try {
                mediaMetadataRetriever.release()
            } catch (e: Exception) {
                // Ignore errors on release
            }
        }
        if (songImage == null) { // Fallback image
            songImage = BitmapFactory.decodeResource(context.resources, R.drawable.default_music_icon)
        }


        // Create actions using MediaButtonReceiver for standard controls
        val playPauseAction = NotificationCompat.Action(
            if (isPlaying) R.drawable.ic_pause_black_24dp else R.drawable.ic_play_arrow_black_24dp,
            if (isPlaying) "Pause" else "Play",
            MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                if (isPlaying) android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE else android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY)
        )

        val nextAction = NotificationCompat.Action(
            R.drawable.ic_skip_next_black_24dp,
            "Next",
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        )

        val previousAction = NotificationCompat.Action(
            R.drawable.ic_skip_previous_black_24dp,
            "Previous",
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        )

        // Example of a custom action like stop (if not handled by MediaButtonReceiver directly for your needs)
        val stopIntent = Intent(context, MusicService::class.java).apply { action = MusicService.ACTION_STOP }
        val stopPendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        val stopAction = NotificationCompat.Action(
            R.drawable.ic_stop, // Replace with your stop icon
            "Stop",
            PendingIntent.getService(context, 0, stopIntent, stopPendingIntentFlags)
        )


        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(song.songName)
            .setContentText(song.artistName)
            .setSubText(song.albumName) // Example: display album name
            .setSmallIcon(R.mipmap.ic_small_notif_png_foreground) // Ensure this icon exists
            .setLargeIcon(songImage)
            .setContentIntent(contentIntent)
            .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP)) // When notification is dismissed
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            // .addAction(stopAction) // Optionally add stop action
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionToken)
                    .setShowActionsInCompactView(0, 1, 2) // Indices of actions to show in compact view
                    // .setShowCancelButton(true) // Deprecated, use setCancelButtonIntent
                    // .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)) // If you want a cancel button
            )
            .setOngoing(isPlaying) // Notification stays if playing, dismissable if paused
            .build()
    }
}
