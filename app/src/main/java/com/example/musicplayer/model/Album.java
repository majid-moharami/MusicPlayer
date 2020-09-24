package com.example.musicplayer.model;

import java.io.Serializable;
import java.util.List;

public class Album implements Serializable {
    private List<String> mSongsOfAlbum;
    private String mAlbumName;

    public Album(List<String> songsOfAlbum, String albumName) {
        mSongsOfAlbum = songsOfAlbum;
        mAlbumName = albumName;
    }

    public Album() {
    }

    public List<String> getSongsOfAlbum() {
        return mSongsOfAlbum;
    }

    public void setSongsOfAlbum(List<String> songsOfAlbum) {
        mSongsOfAlbum = songsOfAlbum;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }
}
