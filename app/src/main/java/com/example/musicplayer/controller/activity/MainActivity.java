package com.example.musicplayer.controller.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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
import com.example.musicplayer.controller.fragment.SongsFragment;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.util.MusicState;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SongsFragment.InitNavigationPlayMusicCallback {

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
    private MediaMetadataRetriever mMediaMetadataRetriever=new MediaMetadataRetriever();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSongRepository = SongRepository.getSongRepository(this);
        findViews();
        allListener();
        setSupportActionBar(mToolbar);
        createViewPager();
        configTabWithViewPager();
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
                Toast.makeText(MainActivity.this, "yep", Toast.LENGTH_SHORT).show();
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
            if (position==0) {
                return SongsFragment.newInstance();
            }else if (position == 1){
                return AlbumListFragment.newInstance();
            }else return ArtistListFragment.newInstance();
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
        mMediaMetadataRetriever.setDataSource(this,song.getUri());
        mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        if (mPic!=null){
            Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length);
            mImageViewSongCover.setImageBitmap(songImage);
        }else mImageViewSongCover.setBackgroundResource(R.drawable.default_image_round);

    }

}
