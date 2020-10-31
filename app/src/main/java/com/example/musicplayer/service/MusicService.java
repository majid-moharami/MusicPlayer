package com.example.musicplayer.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.util.MusicNotification;

import java.io.IOException;

public class MusicService extends Service {
    private static final int REQUEST_CODE_NOTIFICATION = 1;
    private static final int NOTIFICATION_ID = 0;
    private boolean mMusicPlayingState = false;

    private static final String NEXT_ACTION = "GoNext";
    private static final String PREVIOUS_ACTION = "GoPrevious";
    private static final String PAUSE_ACTION = "PlayPause";

    private MediaPlayer mMediaPlayer ;
    private SongRepository mRepository;
    private IBinder mBinder = new LocalBinder();

    private PendingIntent mNextMusicPI;
    private PendingIntent mPauseMusicPI;
    private PendingIntent mPreviousMusicPI;
    private int mCurrentSecondOfMusic;


    @Override
    public void onCreate() {
        super.onCreate();
        mRepository = SongRepository.getSongRepository(this);
        mMediaPlayer = new MediaPlayer();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent.getAction().equals(NEXT_ACTION)){
            try {
                playNextMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (intent.getAction().equals(PREVIOUS_ACTION)){
            try {
                playPreviousMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (intent.getAction().equals(PAUSE_ACTION)){
            playPauseMusic();
        }
        else {
            createPendingIntent();
            try {
                playNextMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            MusicNotification musicNotification = new MusicNotification();
//            musicNotification.setPendingIntents(mNextMusicPI,mPreviousMusicPI,mPauseMusicPI);
//            startForeground(3, musicNotification.getMusicNotification(this , mRepository.getCurrentSong()));
        }


        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public MusicService getLocalService() {
            return MusicService.this;
        }
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context , MusicService.class);
        return intent;
    }

    private void createPendingIntent(){
        Intent next = new Intent(MusicService.this , MusicBroadcastReceiver.class);
        next.setAction(NEXT_ACTION);
        mNextMusicPI = PendingIntent.getBroadcast(
                MusicService.this ,
                0 ,
                next ,
                0);

        Intent previous = new Intent(MusicService.this , MusicBroadcastReceiver.class);
        previous.setAction(PREVIOUS_ACTION);
        mPreviousMusicPI = PendingIntent.getBroadcast(
                MusicService.this ,
                0 ,
                previous ,
                0);

        Intent pause = new Intent(MusicService.this , MusicBroadcastReceiver.class);
        pause.setAction(PAUSE_ACTION);
        mPauseMusicPI = PendingIntent.getBroadcast(
                MusicService.this ,
                0 ,
                pause ,
                0);
    }



    private void playPauseMusic() {
        boolean isPlaying = mMediaPlayer.isPlaying();
        if (isPlaying){
            mMediaPlayer.pause();
            mCurrentSecondOfMusic = mMediaPlayer.getCurrentPosition();
        }else {
            mMediaPlayer.seekTo(mCurrentSecondOfMusic);
            mMediaPlayer.start();
        }
        MusicNotification musicNotification = new MusicNotification();
        musicNotification.setPendingIntents(mNextMusicPI,mPreviousMusicPI,mPauseMusicPI);
        startForeground(3, musicNotification.getMusicNotification(this, mRepository.getCurrentSong() , mMediaPlayer.isPlaying()));
    }

    private void playPreviousMusic() throws IOException {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(this, mRepository.playPreviousMusic().getUri());
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        MusicNotification musicNotification = new MusicNotification();
        musicNotification.setPendingIntents(mNextMusicPI,mPreviousMusicPI,mPauseMusicPI);
        startForeground(3, musicNotification.getMusicNotification(this, mRepository.getCurrentSong() , true));
    }

    private void playNextMusic() throws IOException {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(this, mRepository.playNextMusic().getUri());
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        MusicNotification musicNotification = new MusicNotification();
        musicNotification.setPendingIntents(mNextMusicPI,mPreviousMusicPI,mPauseMusicPI);
        startForeground(3, musicNotification.getMusicNotification(this, mRepository.getCurrentSong() , true ));
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    //
//    public class ServiceBroadcast extends BroadcastReceiver {
//
//        private static final String NEXT_ACTION = "GoNext";
//        private static final String PREVIOUS_ACTION = "GoPrevious";
//        private static final String PAUSE_ACTION = "PlayPause";
//
//        private MusicService mService = MusicService.this;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            switch (action){
//                case NEXT_ACTION:
//                    try {
//                        playNextMusic();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case PREVIOUS_ACTION:
//                    playPreviousMusic();
//                    break;
//                case PAUSE_ACTION:
//                    playPauseMusic();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
}
