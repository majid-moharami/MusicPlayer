package com.example.musicplayer.controller.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.activity.MusicSingleTrackActivity;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.util.GifImageView;
import com.example.musicplayer.util.MusicState;
import com.jgabrielfreitas.core.BlurImageView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SingleTrackPlayFragment extends Fragment {


    private SongRepository mRepository;
    private MediaPlayer mMediaPlayer;
    private Song mSong;
    private List<Song> mSongList = new ArrayList<>();

    private Handler mHandler = new Handler();

    private MusicState mMusicState;


    private RoundedImageView mTrackCover;
    private SeekBar mSeekBar;
    private ImageView mPause,mNext,mPrevious,mShuffle,mRepeat;
    private BlurImageView mImageViewBackground;
    private TextView mCurrentTime,mTrackTime,mTrackTitle,mTrackArtist;
    private int mCurrentTimeInt,mDuration;

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
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_single_track_play, container, false);
        findViews(view);
        initUI();
        allListener();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
        mShuffle = view.findViewById(R.id.imageView_shuffle);
        mRepeat = view.findViewById(R.id.imageView_repeat);
        mPrevious = view.findViewById(R.id.per_button_single_track);
        mImageViewBackground = view.findViewById(R.id.blur_image);
    }

    private void allListener(){
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( fromUser){
                    mRepository.getMediaPlayer().seekTo(progress);
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

            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mMediaPlayer = null;
                    mRepository.playNextMusic();
                    mMediaPlayer = mRepository.getMediaPlayer();
                    mMusicState = MusicState.IS_PLAYING;
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
                    mMusicState = MusicState.IS_PLAYING;
                    initUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // mListenerCallback.onClickChangeMusic(mRepository.getCurrentSong().getUUID());

            }
        });
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRepository.getShuffleState() == MusicState.IS_SHUFFLE){
                    mRepository.setShuffleState(MusicState.IS_NOT_SHUFFLE);
                    mShuffle.setImageResource(R.drawable.shuffle_gray);
                }else {
                    mRepository.setShuffleState(MusicState.IS_SHUFFLE);
                    mShuffle.setImageResource(R.drawable.shuffle_red);
                }

            }
        });

        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRepository.getRepeatState() == MusicState.IS_REPEAT){
                    mRepository.setRepeatState(MusicState.IS_NOT_REPEAT);
                    mRepeat.setImageResource(R.mipmap.ic_launcher_repeat_gray_foreground);
                }else {
                    mRepository.setRepeatState(MusicState.IS_REPEAT);
                    mRepeat.setImageResource(R.mipmap.ic_launcher_repeat_red_foreground);
                }            }
        });

    }

    private void initUI() {
        mSong = mRepository.getCurrentSong();
        mTrackTitle.setText(mSong.getSongName());
        mTrackArtist.setText(mSong.getArtistName());
        mTrackTime.setText(timerStringCal(mMediaPlayer.getDuration()));

        imageSongAndBackground(mSong);

        //connectTrackToSeekBar();
        updateUISeekBar();

        if (mMusicState.equals(MusicState.IS_PLAYING))
            mPause.setImageResource(R.drawable.pause_round);
        else mPause.setImageResource(R.drawable.play_round);

        if (mRepository.getShuffleState() == MusicState.IS_SHUFFLE) {
            mShuffle.setImageResource(R.drawable.shuffle_red);
        } else {
            mShuffle.setImageResource(R.drawable.shuffle_gray);
        }
        if (mRepository.getRepeatState() == MusicState.IS_REPEAT) {
            mRepeat.setImageResource(R.mipmap.ic_launcher_repeat_red_foreground);
        } else {
            mRepeat.setImageResource(R.mipmap.ic_launcher_repeat_gray_foreground);
        }
    }

    private void imageSongAndBackground(Song song) {
        MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
        mMediaMetadataRetriever.setDataSource(song.getPath());
        byte[] mPic = mMediaMetadataRetriever.getEmbeddedPicture();
        if (mPic!=null){
            BitmapFactory.Options bitmapFactory = new BitmapFactory.Options();
            bitmapFactory.outWidth = mTrackCover.getMaxWidth();
            bitmapFactory.outHeight = mTrackCover.getMaxHeight();
            Bitmap songImage = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory);
            mTrackCover.setImageBitmap(songImage);
            BitmapFactory.Options bitmapFactory1 = new BitmapFactory.Options();

            //blur background
            bitmapFactory1.outWidth =getActivity().getWindowManager().getDefaultDisplay().getWidth();
            bitmapFactory1.outHeight = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            Bitmap songImage1 = BitmapFactory.decodeByteArray(mPic, 0, mPic.length, bitmapFactory1);
            mImageViewBackground.setImageBitmap(songImage1);
            mImageViewBackground.setBlur(20);
        }
    }

    private void updateUISeekBar() {
        mCurrentTimeInt = mRepository.getMediaPlayer().getCurrentPosition();
        mDuration = mRepository.getMediaPlayer().getDuration();
        mSeekBar.setMax(mDuration);
        mSeekBar.setProgress(mCurrentTimeInt);
        mHandler.postDelayed(updateAudioTime, 1000);
    }
    private Runnable updateAudioTime = new Runnable() {
        @Override
        public void run() {
            mCurrentTimeInt = mRepository.getMediaPlayer().getCurrentPosition();
            updateControls(mCurrentTimeInt, mDuration);
            mHandler.postDelayed(this, 1000);
        }
    };

    private void updateControls(int current, int mDuration) {
        mCurrentTime.setText(timerStringCal(mRepository.getMediaPlayer().getCurrentPosition()));
        mSeekBar.setProgress(current);
    }


//    private void connectTrackToSeekBar() {
//        mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if(mMediaPlayer != null){
//                    int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
//                    int mCurrentPosition1 = mMediaPlayer.getDuration() / 1000;
//                    mCurrentTime.setText(timerStringCal(mMediaPlayer.getCurrentPosition()));
//                    mSeekBar.setProgress(mCurrentPosition);
//                    if (mCurrentPosition==mCurrentPosition1 && mRepository.getRepeatState()==MusicState.IS_REPEAT){
//                        try {
//                            mRepository.setCurrentSong(mRepository.getCurrentSong());
//                            mRepository.playMusicWithoutShuffle();
//                            mMediaPlayer = mRepository.getMediaPlayer();
//                            initUI();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getActivity(), "not", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                }
//                mHandler.postDelayed(this, 1000);
//            }
//        });
//
//    }


    private String timerStringCal(int time){
        int min = time/1000/60;
        int second = time/1000%60;

        String timeString = min + ":";

        if (second<10)
            timeString += " 0";
        timeString+=second;

        return timeString;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mRunnable=null;
        mMediaPlayer=null;
    }
}