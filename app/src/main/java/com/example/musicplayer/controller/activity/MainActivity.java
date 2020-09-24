package com.example.musicplayer.controller.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.fragment.AlbumListFragment;
import com.example.musicplayer.controller.fragment.ArtistListFragment;
import com.example.musicplayer.controller.fragment.SingleTrackPlayFragment;
import com.example.musicplayer.controller.fragment.SongsFragment;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.util.MusicState;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.intentfilter.androidpermissions.PermissionManager;
import com.intentfilter.androidpermissions.models.DeniedPermission;
import com.intentfilter.androidpermissions.models.DeniedPermissions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static java.util.Collections.singleton;

public class MainActivity extends AppCompatActivity implements
        SongsFragment.InitNavigationPlayMusicCallback, SingleTrackPlayFragment.MusicChangeState {

    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private View mIncludeLayout;
    private RoundedImageView mImageViewSongCover;
    private ImageView mImageButtonNext;
    private ImageView mImageButtonPerv;
    private ImageView mImageButtonPause;
    private TextView mTextViewArtist;
    private TextView mTextViewSongName;
    private View mCurrentMusicPlaying;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private SongRepository mSongRepository;
    private List<Song> mAllMusic;
    private MusicState mMusicState = MusicState.IS_PAUSE;
    private byte[] mPic;
    private MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionManager permissionManager = PermissionManager.getInstance(this);
        permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                mSongRepository = SongRepository.getSongRepository(MainActivity.this);
                findViews();
                allListener();
                setSupportActionBar(mToolbar);
                createViewPager();
                configTabWithViewPager();
            }

            @Override
            public void onPermissionDenied(DeniedPermissions deniedPermissions) {
                String deniedPermissionsText = "Denied: " + Arrays.toString(deniedPermissions.toArray());
                Toast.makeText(MainActivity.this, deniedPermissionsText, Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();
                for (DeniedPermission deniedPermission : deniedPermissions) {
                    if(deniedPermission.shouldShowRationale()) {
                        // Display a rationale about why this permission is required
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

//
//        mMediaMetadataRetriever.setDataSource(this, mSongRepository.getCurrentSong().getUri());
//        mPic = mMediaMetadataRetriever.getEmbeddedPicture();
//        if (mPic != null) {
//            Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length);
//            mImageViewSongCover.setImageBitmap(songImage);
//        } else mImageViewSongCover.setBackgroundResource(R.drawable.default_image_round);
    }



    private void findViews() {
        mViewPager2 = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_bar);
        mToolbar = findViewById(R.id.music_toolbar);
        mIncludeLayout = findViewById(R.id.inclide_paly_bar);
        mImageButtonNext = findViewById(R.id.next_button);
        mImageButtonPerv = findViewById(R.id.per_button);
        mImageButtonPause = findViewById(R.id.puse_button);
        mImageViewSongCover = findViewById(R.id.cover_track);
        mTextViewArtist = findViewById(R.id.artist_name);
        mTextViewSongName = findViewById(R.id.song_name);
        mCurrentMusicPlaying = findViewById(R.id.current_music_nav);
    }

    private void allListener() {
        mCurrentMusicPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MusicSingleTrackActivity.newIntent(MainActivity.this);
                startActivity(intent);
            }
        });
        mImageButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicState == MusicState.IS_PAUSE) {
                    mMusicState = MusicState.IS_PLAYING;

                    mSongRepository.resumeMusic();

                    mImageButtonPause.setImageResource(R.drawable.pause_round);
                } else {
                    mMusicState = MusicState.IS_PAUSE;
                    mSongRepository.stopMusic();
                    mImageButtonPause.setImageResource(R.drawable.play_round);
                }
            }
        });
    }

    private void configTabWithViewPager() {
        new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0)
                    tab.setText("Songs");
                else if (position == 1)
                    tab.setText("Albums");
                else if (position == 2)
                    tab.setText("Artists");
            }
        }).attach();
    }

    private void createViewPager() {
        FragmentStateAdapter TaskAdapter = new ViewPagerAdapter(this);
        mViewPager2.setAdapter(TaskAdapter);
        // mViewPager2.setPageTransformer(new DepthPageTransformer());
    }




    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return SongsFragment.newInstance(null,null);
            } else if (position == 1) {
                return AlbumListFragment.newInstance();
            } else return ArtistListFragment.newInstance();
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }


    @Override
    public void onClick(UUID uuid) {
        Song song = mSongRepository.getSong(uuid);
        mTextViewSongName.setText(song.getSongName());
        mTextViewArtist.setText(song.getArtistName());
        mImageButtonPause.setImageResource(R.drawable.pause_round);
        mMusicState = MusicState.IS_PLAYING;
        mMediaMetadataRetriever.setDataSource(this, song.getUri());
        mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        if (mPic != null) {
            Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length);
            mImageViewSongCover.setImageBitmap(songImage);
        } else mImageViewSongCover.setBackgroundResource(R.drawable.default_image_round);

    }
    @Override
    public void onClickChangeMusic(UUID uuid) {
        Song song = mSongRepository.getSong(uuid);
        if (mMediaPlayer.isPlaying()){
            mMusicState = MusicState.IS_PLAYING;
            mImageButtonPause.setImageResource(R.drawable.pause_round);
        }else{
            mMusicState = MusicState.IS_PAUSE;
            mImageButtonPause.setImageResource(R.drawable.play_round);
        }
        mTextViewSongName.setText(song.getSongName());
        mTextViewArtist.setText(song.getArtistName());
        mMediaMetadataRetriever.setDataSource(this, song.getUri());
        mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        if (mPic != null) {
            Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length);
            mImageViewSongCover.setImageBitmap(songImage);
        } else mImageViewSongCover.setBackgroundResource(R.drawable.default_image_round);
    }


}
