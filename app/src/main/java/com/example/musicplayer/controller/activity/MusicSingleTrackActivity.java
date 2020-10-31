package com.example.musicplayer.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.musicplayer.controller.activity.SingleFragmentActivity;
import com.example.musicplayer.controller.fragment.SingleTrackPlayFragment;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.service.MusicService;

import java.io.IOException;

public class MusicSingleTrackActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context , MusicSingleTrackActivity.class);
        return intent;
    }
    private SongRepository mRepository;

    private MusicService mMusicService;
    private boolean mBind=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = SongRepository.getSongRepository(MusicSingleTrackActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

            Intent intent = MusicService.newIntent(MusicSingleTrackActivity.this);
            intent.setAction("makod");
            startService(intent);
        Intent intent1 = MusicService.newIntent(this);
        bindService(intent1 , mConnection , 0);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBind = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (!(service instanceof MusicService.LocalBinder)) {
                Log.e("Service", "This is wrong service");
                return;
            }
            MusicService.LocalBinder musicBinder = (MusicService.LocalBinder) service;
            mMusicService = musicBinder.getLocalService();
            mRepository.setMediaPlayer(mMusicService.getMediaPlayer());
            mBind = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public Fragment createFragment() {
        return SingleTrackPlayFragment.newInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            Intent intent = new Intent();
            intent.putExtra("song","ok");
            setResult(RESULT_OK, intent);
    }

}