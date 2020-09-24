package com.example.musicplayer.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.musicplayer.controller.activity.SingleFragmentActivity;
import com.example.musicplayer.controller.fragment.SingleTrackPlayFragment;

public class MusicSingleTrackActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context , MusicSingleTrackActivity.class);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return SingleTrackPlayFragment.newInstance();
    }
}