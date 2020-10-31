package com.example.musicplayer.controller.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.albums.AlbumAdapter;
import com.example.musicplayer.adapter.artists.ArtistAdapter;
import com.example.musicplayer.adapter.folder.FolderAdapter;
import com.example.musicplayer.controller.fragment.AlbumListFragment;
import com.example.musicplayer.controller.fragment.ArtistListFragment;
import com.example.musicplayer.controller.fragment.FolderListFragment;
import com.example.musicplayer.controller.fragment.SongsFragment;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Folder;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.util.MusicState;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.intentfilter.androidpermissions.PermissionManager;
import com.intentfilter.androidpermissions.models.DeniedPermission;
import com.intentfilter.androidpermissions.models.DeniedPermissions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static java.util.Collections.singleton;

public class MainActivity extends AppCompatActivity implements
        SongsFragment.InitNavigationPlayMusicCallback,
        ArtistAdapter.StartMusicListActivity, AlbumAdapter.StartMusicListActivityInAlbum, FolderAdapter.StartMusicListActivityInFolder {

    public static final int REQUEST_CODE_START_SINGLE_TRACK_ACTIVITY = 1;
    public static final int REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_FOR_ARTIST = 2;
    public static final int REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_ALBUM = 3;
    public static final String BUNDLE_MEDIA_PLAYER_POSITION = "bundle_mediaplayer_position";
    public static final String BUNDLE_MEDIA_PLAYER_POSITION1 = BUNDLE_MEDIA_PLAYER_POSITION;
    public static final String BUNDLE_KEY_POSITION_OF_CURRENT_SONG = "position_of_currnt_song";
    public static final String BUNDLE_KEY_CURRENT_SECOND_SONG = "current_second_song";
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
    private MusicState mMusicState = MusicState.IS_PAUSE;
    private byte[] mPic;
    private MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionManager permissionManager = PermissionManager.getInstance(this);
        permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                mSongRepository = SongRepository.getSongRepository(MainActivity.this);
                findViews();
                try {
                    initUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                    if (deniedPermission.shouldShowRationale()) {
                        // Display a rationale about why this permission is required
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(BUNDLE_KEY_CURRENT_SECOND_SONG);
            int positionSong = savedInstanceState.getInt(BUNDLE_KEY_POSITION_OF_CURRENT_SONG);
            Song song = mSongRepository.getSongList().get(positionSong);
            mSongRepository.setCurrentSong(song);
            try {
                mSongRepository.playMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSongRepository.getMediaPlayer().seekTo(position);
            updateNavBar();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO
//        if (resultCode  != Activity.RESULT_OK || data==null )
//            return;

        if (requestCode == REQUEST_CODE_START_SINGLE_TRACK_ACTIVITY ||
                requestCode == REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_FOR_ARTIST ||
                requestCode == REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_ALBUM) {
            updateNavBar();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_POSITION_OF_CURRENT_SONG, mSongRepository.getPosition(mSongRepository.getCurrentSong()));
        outState.putInt(BUNDLE_KEY_CURRENT_SECOND_SONG, mSongRepository.getMediaPlayer().getCurrentPosition());
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
        //Toast.makeText(this, "kji", Toast.LENGTH_SHORT).show();
    }

    private void initUI() throws IOException {
        mSongRepository.setCurrentSong(mSongRepository.getSongList().get(0));
        mSongRepository.playMusic();
        mSongRepository.stopMusic();
        updateNavBar();
    }

    private void allListener() {
        mCurrentMusicPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MusicSingleTrackActivity.newIntent(MainActivity.this);
                startActivityForResult(intent, REQUEST_CODE_START_SINGLE_TRACK_ACTIVITY);
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

        mImageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSongRepository.playNextMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                updateNavBar();

            }
        });

        mImageButtonPerv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSongRepository.playPreviousMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                updateNavBar();
            }
        });
    }

    private void updateNavBar() {
        Song song = mSongRepository.getCurrentSong();
        mMediaPlayer = mSongRepository.getMediaPlayer();
        mTextViewSongName.setText(song.getSongName());
        mTextViewArtist.setText(song.getArtistName());

        if (mMediaPlayer.isPlaying()) {
            mMusicState = MusicState.IS_PLAYING;
            mImageButtonPause.setImageResource(R.drawable.pause_round);
        } else {
            mMusicState = MusicState.IS_PAUSE;
            mImageButtonPause.setImageResource(R.drawable.play_round);
        }


        mMediaMetadataRetriever.setDataSource(this, song.getUri());
        mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        if (mPic != null) {
            Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length);
            mImageViewSongCover.setImageBitmap(songImage);
        } else mImageViewSongCover.setImageResource(R.mipmap.ic_empty_cover_foreground);
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
                else if (position == 3)
                    tab.setText("Folders");
                else if (position == 4)
                    tab.setText("Favorite");
            }
        }).attach();
    }

    private void createViewPager() {
        FragmentStateAdapter TaskAdapter = new ViewPagerAdapter(this);
        mViewPager2.setAdapter(TaskAdapter);
    }



    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return SongsFragment.newInstance(null, null,null);
            } else if (position == 1) {
                return AlbumListFragment.newInstance();
            } else if (position == 2)
                return ArtistListFragment.newInstance();
            else if (position == 3)
                return FolderListFragment.newInstance();
            else
                return ArtistListFragment.newInstance();
        }

        @Override
        public int getItemCount() {
            return 5;
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
        } else mImageViewSongCover.setImageResource(R.mipmap.ic_empty_cover_foreground);

    }

    @Override
    public void startForArtistCallBack(Artist artist) {
        Intent intent = MusicListActivity.newIntent(this, null, artist , null);
        startActivityForResult(intent, REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_FOR_ARTIST);
    }

    @Override
    public void startForAlbumCallBack(Album album) {
        Intent intent = MusicListActivity.newIntent(this, album, null,null);
        startActivityForResult(intent, REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_ALBUM);
    }

    @Override
    public void startForFolderCallBack(Folder folder) {
        Intent intent = MusicListActivity.newIntent(this, null, null,folder);
        startActivityForResult(intent, REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_ALBUM);
    }
}
