package com.example.musicplayer.model;

import java.io.Serializable;
import java.util.List;

public class Artist implements Serializable {
    private List<String> mSongsOfArtist;
    private String mArtistName;

    public Artist(List<String> songsOfArtist, String artistName) {
        mSongsOfArtist = songsOfArtist;
        mArtistName = artistName;
    }

    public Artist() {
    }

    public List<String> getSongsOfArtist() {
        return mSongsOfArtist;
    }

    public void setSongsOfArtist(List<String> songsOfArtist) {
        mSongsOfArtist = songsOfArtist;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }
}
