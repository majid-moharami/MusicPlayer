package com.example.musicplayer.controller.activity;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.musicplayer.R;

/**
 * If we have activity that has only one fullscreen fragement we must
 * extend this Activity
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container2,createFragment())
                .commit();

    }
    public abstract Fragment createFragment();
}