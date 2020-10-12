package com.example.musicplayer.model;

import java.io.Serializable;
import java.util.List;

public class Folder implements Serializable {
    private String mFolderName;
    private List<String> mSongsFolderPath;

    public Folder(String mFolderName, List<String> mSongsFolderPath) {
        this.mFolderName = mFolderName;
        this.mSongsFolderPath = mSongsFolderPath;
    }

    public String getFolderName() {
        return mFolderName;
    }

    public void setFolderName(String mFolderName) {
        this.mFolderName = mFolderName;
    }

    public List<String> getSongsFolderPath() {
        return mSongsFolderPath;
    }

    public void setSongsFolderPath(List<String> mSongsFolderPath) {
        this.mSongsFolderPath = mSongsFolderPath;
    }
}
