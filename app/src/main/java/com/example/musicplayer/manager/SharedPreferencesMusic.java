package com.example.musicplayer.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicplayer.model.Song;

public class SharedPreferencesMusic {

    public static final String CURRENT_SONG = "currentSong";
    private static final String MY_PREFERENCE= "MyPreference";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SharedPreferencesMusic(Context context) {
        mSharedPreferences = context.getSharedPreferences( MY_PREFERENCE , Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void saveCurrentMusic(Song song){
        mEditor.putString(CURRENT_SONG, song.getUUID().toString());
        mEditor.commit();
    }
}
