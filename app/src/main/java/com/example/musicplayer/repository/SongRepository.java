package com.example.musicplayer.repository;

import android.content.Context;

import com.example.musicplayer.model.Song;
import com.example.musicplayer.util.SongLoader;

import java.util.List;

public class SongRepository {
    private static SongRepository sSongRepository;
    private static Context mContext;
    private List<Song> mSongList;

    public static SongRepository getSongRepository(Context context) throws Exception {
        mContext = context.getApplicationContext();
        if (sSongRepository==null)
             sSongRepository = new SongRepository();

        return sSongRepository;
    }

    private SongRepository() throws Exception {
        mSongList=SongLoader.loadAllSong(mContext);
    }

    public List<Song> getSongList() {



        return mSongList;
    }

    public void setSongList(List<Song> songList) {
        mSongList = songList;
    }

    public Song getSong(int position){
        return mSongList.get(position);
    }
}
