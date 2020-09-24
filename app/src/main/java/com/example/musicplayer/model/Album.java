package com.example.musicplayer.model;

import java.util.List;

public class Album {
    private List<Song> mSongsOfAlbum;
    private String mAlbumName;

    public Album(List<Song> songsOfAlbum, String albumName) {
        mSongsOfAlbum = songsOfAlbum;
        mAlbumName = albumName;
    }

    public List<Song> getSongsOfAlbum() {
        return mSongsOfAlbum;
    }

    public void setSongsOfAlbum(List<Song> songsOfAlbum) {
        mSongsOfAlbum = songsOfAlbum;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }
}
