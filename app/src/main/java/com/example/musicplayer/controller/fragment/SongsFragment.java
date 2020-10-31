package com.example.musicplayer.controller.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.songs.SongsAdapter;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Folder;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.util.PlayMusicRole;
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

    public static final String ARGS_KEY_ABLBUM = "ARGS_KEY_ABLBUM";
    public static final String ARGS_KEY_ARTIST = "ARGS_KEY_ARTIST";
    public static final String ARGS_KEY_FOLDER = "ARGS_KEY_FOLDER";
    private FastScrollRecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private SongRepository mSongRepository;
    private List<Song> mAllMusic = new ArrayList<>();
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private InitNavigationPlayMusicCallback mCallback;
    private InitFloatingImageCover mCover;


    public static SongsFragment newInstance(Album album, Artist artist, Folder folder) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_KEY_ABLBUM, album);
        args.putSerializable(ARGS_KEY_ARTIST, artist);
        args.putSerializable(ARGS_KEY_FOLDER, folder);
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
        initUi();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        updateUI();
        return view;
    }

    private void initUi() {
        Album album = (Album) getArguments().getSerializable(ARGS_KEY_ABLBUM);
        Artist artist = (Artist) getArguments().getSerializable(ARGS_KEY_ARTIST);
        Folder folder = (Folder) getArguments().getSerializable(ARGS_KEY_FOLDER);
        if (album == null && artist == null && folder == null) {
            mAllMusic = mSongRepository.getSongList();
        } else {
            if (album != null) {
                if (album.getSongsOfAlbum().size() != 0) {
                    for (int i = 0; i < mSongRepository.getSongList().size(); i++) {
                        for (int j = 0; j < album.getSongsOfAlbum().size(); j++) {
                            if (mSongRepository.getSongList().get(i).getPath().equals(album.getSongsOfAlbum().get(j))) {
                                mAllMusic.add(mSongRepository.getSongList().get(i));
                            }
                        }
                    }
                    mSongRepository.setCurrentAlbumSongs(mAllMusic);
                }
                //mAllMusic = album.getSongsOfAlbum();
            } else if (artist != null) {
                if (artist.getSongsOfArtist().size() != 0) {
                    for (int i = 0; i < mSongRepository.getSongList().size(); i++) {
                        for (int j = 0; j < artist.getSongsOfArtist().size(); j++) {
                            if (mSongRepository.getSongList().get(i).getPath().equals(artist.getSongsOfArtist().get(j))) {
                                mAllMusic.add(mSongRepository.getSongList().get(i));
                            }
                        }
                    }
                    mSongRepository.setCurrentArtistSongs(mAllMusic);
                }
                //mAllMusic = artist.getSongsOfArtist();
            } else if (folder != null) {
                if (folder.getSongsFolderPath().size() != 0) {
                    for (int i = 0; i < mSongRepository.getSongList().size(); i++) {
                        for (int j = 0; j < folder.getSongsFolderPath().size(); j++) {
                            if (mSongRepository.getSongList().get(i).getPath().equals(folder.getSongsFolderPath().get(j))) {
                                mAllMusic.add(mSongRepository.getSongList().get(i));
                            }
                        }
                    }
                    mSongRepository.setCurrentFolderSongs(mAllMusic);
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InitNavigationPlayMusicCallback) {
            mCallback = (InitNavigationPlayMusicCallback) context;
        }
        if (context instanceof InitFloatingImageCover){
            mCover = (InitFloatingImageCover) context;
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
            mAdapter = new SongsAdapter(mAllMusic, this, getActivity());
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
        Album album = (Album) getArguments().getSerializable(ARGS_KEY_ABLBUM);
        Artist artist = (Artist) getArguments().getSerializable(ARGS_KEY_ARTIST);
        Folder folder = (Folder) getArguments().getSerializable(ARGS_KEY_FOLDER);
        if (album == null && artist == null && folder==null)
            mCallback.onClick(song.getUUID());
    }

    public interface InitNavigationPlayMusicCallback {
        void onClick(UUID uuid);
    }

    public interface InitFloatingImageCover {
        void onTime(String path);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSongRepository.setIsMain(true);
    }
}