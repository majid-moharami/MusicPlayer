package com.example.musicplayer.controller.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.util.MusicState;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SingleTrackPlayFragment extends Fragment {

    private MusicChangeState mListenerCallback;

    private SongRepository mRepository;
    private MediaPlayer mMediaPlayer;
    private Song mSong;
    private List<Song> mSongList = new ArrayList<>();

    private Handler mHandler;
    private Runnable mRunnable;

    private MusicState mMusicState;

    private RoundedImageView mTrackCover;
    private SeekBar mSeekBar;
    private ImageView mPause,mNext,mPrevious;
    private TextView mCurrentTime,mTrackTime,mTrackTitle,mTrackArtist;
    private ConstraintLayout mLayout;

    public static SingleTrackPlayFragment newInstance() {
        SingleTrackPlayFragment fragment = new SingleTrackPlayFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = SongRepository.getSongRepository(getActivity());
        mSongList = mRepository.getSongList();
        mMediaPlayer = mRepository.getMediaPlayer();
        if (mMediaPlayer.isPlaying()){
            mMusicState = MusicState.IS_PLAYING;
        }else mMusicState = MusicState.IS_PAUSE;
        mHandler = new Handler();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_single_track_play, container, false);
        findViews(view);
        AnimationDrawable frameAnimation = (AnimationDrawable) mLayout.getBackground();
        frameAnimation.setEnterFadeDuration(2000);
        frameAnimation.setExitFadeDuration(4000);
        frameAnimation.start();
        initUI();
        allListener();
        return view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MusicChangeState){
            mListenerCallback = (MusicChangeState) context;
        }
    }

    private void findViews(View view) {
        mTrackCover = view.findViewById(R.id.cover_track_single);
        mSeekBar = view.findViewById(R.id.seekBar_single_track);
        mTrackTitle = view.findViewById(R.id.single_track_title);
        mTrackArtist = view.findViewById(R.id.single_track_artist);
        mTrackTime = view.findViewById(R.id.track_time);
        mCurrentTime = view.findViewById(R.id.current_time_track);
        mPause = view.findViewById(R.id.resume_pause_single_track);
        mNext = view.findViewById(R.id.next_button_single_track);
        mPrevious = view.findViewById(R.id.per_button_single_track);
        mLayout = view.findViewById(R.id.root);
    }

    private void allListener(){
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mMediaPlayer != null && fromUser){
                    mMediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicState == MusicState.IS_PAUSE) {
                    mMusicState = MusicState.IS_PLAYING;

                    mRepository.resumeMusic();

                    mPause.setImageResource(R.drawable.pause_round);
                } else {
                    mMusicState = MusicState.IS_PAUSE;
                    mRepository.stopMusic();
                    mPause.setImageResource(R.drawable.play_round);
                }

                //mListenerCallback.onClickChangeMusic(mRepository.getCurrentSong().getUUID());
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mRepository.playNextMusic();
                    mMediaPlayer = mRepository.getMediaPlayer();
                    initUI();

                } catch (IOException e) {
                    e.printStackTrace();
                }
               // mListenerCallback.onClickChangeMusic(mRepository.getCurrentSong().getUUID());

            }
        });
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mRepository.playPreviousMusic();
                    mMediaPlayer = mRepository.getMediaPlayer();
                    initUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // mListenerCallback.onClickChangeMusic(mRepository.getCurrentSong().getUUID());

            }
        });
    }

    private void initUI() {
        mSong = mRepository.getCurrentSong();
        mTrackTitle.setText(mSong.getSongName());
        mTrackArtist.setText(mSong.getArtistName());

        MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
        mMediaMetadataRetriever.setDataSource(mSong.getPath());
        byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        if (mPic!=null){
            BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
            bitmapFactory.outWidth = mTrackCover.getMaxWidth();
            bitmapFactory.outHeight = mTrackCover.getMaxHeight();
            Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
            mTrackCover.setImageBitmap(songImage);
        }

        connectTrackToSeekBar();

        if (mMusicState.equals(MusicState.IS_PLAYING))
            mPause.setImageResource(R.drawable.pause_round);
        else mPause.setImageResource(R.drawable.play_round);
    }

    private void connectTrackToSeekBar() {
        mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mMediaPlayer != null){
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                    mSeekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

//        mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (mMediaPlayer != null) {
//                    int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000; // In milliseconds
//                    mSeekBar.setProgress(mCurrentPosition);
//                }
//                //mHandler.postDelayed(mRunnable, 1000);
//            }
//        };
//        mHandler.postDelayed(mRunnable, 1000);
    }

    public interface MusicChangeState {
        void onClickChangeMusic(UUID uuid);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRunnable=null;
        mMediaPlayer=null;
    }
}