package com.example.musicplayer.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.fragment.AlbumListFragment;
import com.example.musicplayer.controller.fragment.SingleTrackPlayFragment;
import com.example.musicplayer.controller.fragment.SongsFragment;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;

public class MusicListActivity extends SingleFragmentActivity {

    public static final String EXTRA_KEY_ALBUM = "extra_key_album";
    public static final String EXTRA_KEY_ARTIST = "extra_key_artist";

    public static Intent newIntent(Context context, Album album , Artist artist){
        Intent intent = new Intent(context , MusicListActivity.class);
        intent.putExtra(EXTRA_KEY_ALBUM ,album);
        intent.putExtra(EXTRA_KEY_ARTIST, artist);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return SongsFragment.newInstance(
                (Album) getIntent().getSerializableExtra(EXTRA_KEY_ALBUM),
                (Artist) getIntent().getSerializableExtra(EXTRA_KEY_ARTIST)
        );
    }


}