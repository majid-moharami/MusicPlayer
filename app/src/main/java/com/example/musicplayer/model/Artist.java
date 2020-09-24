package com.example.musicplayer.model;

import java.util.List;

public class Artist {
    private List<Song> mSongsOfArtist;
    private String mArtistName;

    public Artist(List<Song> songsOfArtist, String artistName) {
        mSongsOfArtist = songsOfArtist;
        mArtistName = artistName;
    }

    public List<Song> getSongsOfArtist() {
        return mSongsOfArtist;
    }

    public void setSongsOfArtist(List<Song> songsOfArtist) {
        mSongsOfArtist = songsOfArtist;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }
}
