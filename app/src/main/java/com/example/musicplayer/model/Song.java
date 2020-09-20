package com.example.musicplayer.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.UUID;

public class Song implements Serializable {

    private String mSongName;
    private String mArtistName;
    private String mAlbumName;
    private int mTrackDuration;
    private Uri mUri;
    private Uri mTrackCover;
    private UUID mUUID;

    public Song(String songName, String artistName,String albumName,Uri uri) {
        mSongName = songName;
        mArtistName = artistName;
        mAlbumName=albumName;
        mUUID = UUID.randomUUID();
        mUri=uri;
    }

    public int getTrackDuration() {
        return mTrackDuration;
    }

    public void setTrackDuration(int trackDuration) {
        mTrackDuration = trackDuration;
    }

    public Uri getTrackCover() {
        return mTrackCover;
    }

    public void setTrackCover(Uri trackCover) {
        mTrackCover = trackCover;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        mSongName = songName;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }

}
