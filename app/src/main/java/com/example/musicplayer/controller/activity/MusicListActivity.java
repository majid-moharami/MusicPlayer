package com.example.musicplayer.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.fragment.SongsFragment;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Folder;

public class MusicListActivity extends AppCompatActivity implements SongsFragment.InitFloatingImageCover {

    public static final String EXTRA_KEY_ALBUM = "extra_key_album";
    public static final String EXTRA_KEY_ARTIST = "extra_key_artist";
    public static final String EXTRA_KEY_FOLDER = "extra_key_folder";
    private ImageView mFloatingImageCover;

    public static Intent newIntent(Context context, Album album, Artist artist, Folder folder) {
        Intent intent = new Intent(context, MusicListActivity.class);
        intent.putExtra(EXTRA_KEY_ALBUM, album);
        intent.putExtra(EXTRA_KEY_ARTIST, artist);
        intent.putExtra(EXTRA_KEY_FOLDER, folder);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);
        mFloatingImageCover = findViewById(R.id.floating_image_cover);

        Album album = (Album) getIntent().getSerializableExtra(EXTRA_KEY_ALBUM);
        Artist artist = (Artist) getIntent().getSerializableExtra(EXTRA_KEY_ARTIST);
        Folder folder = (Folder) getIntent().getSerializableExtra(EXTRA_KEY_FOLDER);

        if (album != null) {
            setCover(album.getSongsOfAlbum().get(0));
        }
        if (artist != null) {
            setCover(artist.getSongsOfArtist().get(0));
        }
        if (folder != null) {
            setCover(folder.getSongsFolderPath().get(0));
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container3, SongsFragment.newInstance(
                        (Album) getIntent().getSerializableExtra(EXTRA_KEY_ALBUM),
                        (Artist) getIntent().getSerializableExtra(EXTRA_KEY_ARTIST),
                        (Folder) getIntent().getSerializableExtra(EXTRA_KEY_FOLDER)
                ))
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("song", "ok");
        setResult(RESULT_OK, intent);
    }


    @Override
    public void onTime(String path) {
        MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
        mMediaMetadataRetriever.setDataSource(path);
        final byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        if (mPic != null) {
            final BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
            bitmapFactory.outWidth = 50;
            bitmapFactory.outHeight = 50;
            final Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
            mFloatingImageCover.setImageBitmap(songImage);
        }
    }

    private void setCover(String path) {
        MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
        mMediaMetadataRetriever.setDataSource(path);
        final byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        if (mPic != null) {
            final BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
            bitmapFactory.outWidth = 50;
            bitmapFactory.outHeight = 50;
            final Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
            mFloatingImageCover.setImageBitmap(songImage);
        }
    }
}