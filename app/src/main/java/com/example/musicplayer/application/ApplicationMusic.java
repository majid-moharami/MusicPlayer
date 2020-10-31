package com.example.musicplayer.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.musicplayer.R;

public class ApplicationMusic extends Application {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String channelId = getResources().getString(R.string.notification_channel_id);
        String channelName = "majid_music_player";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                importance);
        channel.enableLights(false);
        channel.enableVibration(false);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(channel);
    }
}
