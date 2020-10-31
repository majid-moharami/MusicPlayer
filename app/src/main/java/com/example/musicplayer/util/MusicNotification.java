package com.example.musicplayer.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.activity.MusicSingleTrackActivity;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;

public class MusicNotification {
    private static final int REQUEST_CODE_NOTIFICATION = 1;
    private PendingIntent mNext;
    private PendingIntent mPause;
    private PendingIntent mPrevious;


    public  android.app.Notification getMusicNotification(Context context ,Song song , boolean musicIsPlaying){

        MediaSessionCompat mediaSession  = new MediaSessionCompat(context , "tag");
        String channelId = "majid.m";
        MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
        mMediaMetadataRetriever.setDataSource(song.getPath());
        final byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        Bitmap songImage = null;
        if (mPic != null){
            final BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
            bitmapFactory.outWidth =50;
            bitmapFactory.outHeight = 50;
           songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                REQUEST_CODE_NOTIFICATION,
                MusicSingleTrackActivity.newIntent(context),
                0);

        android.app.Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_small_notif_png_foreground)
                .setLargeIcon(songImage)
                .addAction(R.drawable.ic_skip_previous_black_24dp,"previous" , mPrevious ) // #0
                .addAction(musicIsPlaying ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp ,
                        "pause" ,
                        mPause ) // #1
                .addAction(R.drawable.ic_skip_next_black_24dp,"next" , mNext ) // #2
                .setContentTitle(song.getSongName())
                .setContentText(song.getArtistName())
                .setContentIntent(pendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSession.getSessionToken())).build();

        return notification;
    }

    public void setPendingIntents(PendingIntent nextPI , PendingIntent previousPI , PendingIntent pausePI){
        mNext = nextPI;
        mPause = pausePI;
        mPrevious = previousPI;
    }
}
