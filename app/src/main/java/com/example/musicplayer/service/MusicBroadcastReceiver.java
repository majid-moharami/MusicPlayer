package com.example.musicplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.musicplayer.controller.activity.MusicSingleTrackActivity;

import java.io.IOException;

public class MusicBroadcastReceiver extends BroadcastReceiver {

    private static final String NEXT_ACTION = "GoNext";
    private static final String PREVIOUS_ACTION = "GoPrevious";
    private static final String PAUSE_ACTION = "PlayPause";

    private MusicService mService;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = MusicService.newIntent(context);
        intent1.setAction(intent.getAction());
        context.startService(intent1);
    }
}
