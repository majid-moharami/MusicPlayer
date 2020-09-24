package com.example.musicplayer.controller.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.songs.SongsAdapter;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsFragment extends Fragment implements SongsAdapter.PlayMusicCallback {

    private FastScrollRecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private SongRepository mSongRepository;
    private List<Song> mAllMusic = new ArrayList<>();
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private InitNavigationPlayMusicCallback mCallback;


    public static SongsFragment newInstance() {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongRepository = SongRepository.getSongRepository(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        findViews(view);
        mAllMusic = mSongRepository.getSongList();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        updateUI();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InitNavigationPlayMusicCallback) {
            mCallback = (InitNavigationPlayMusicCallback) context;
        }
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.song_recycler);
    }

    private void updateUI() {

        if (mAllMusic.size() == 0) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = new SongsAdapter(mAllMusic, this,getActivity());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setSongList(mAllMusic);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(UUID uuid) throws IOException {
        Song song = mSongRepository.getSong(uuid);
        mSongRepository.setCurrentSong(song);
        mSongRepository.playMusic();
        mCallback.onClick(song.getUUID());
    }

    public interface InitNavigationPlayMusicCallback {
        void onClick(UUID uuid);
    }
}