package com.example.musicplayer.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.util.SongLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SongRepository {
    private static SongRepository sSongRepository;
    private static Context mContext;
    private List<Song> mSongList;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Song mCurrentSong;
    private int mCurrentSecondOfMusic;


    public static SongRepository getSongRepository(Context context)  {
        mContext = context.getApplicationContext();
        if (sSongRepository==null)
             sSongRepository = new SongRepository();

        return sSongRepository;
    }

    private SongRepository() {
        mSongList=SongLoader.loadAllSong(mContext);
    }

    public List<Song> getSongList() {
        return mSongList;
    }

    public Song getSong(UUID uuid){
        for (int i = 0; i <mSongList.size() ; i++) {
            if (mSongList.get(i).getUUID()==uuid){
                return mSongList.get(i);
            }
        }
        return null;
    }

    public Song getSongFromPath(String path){
        for (int i = 0; i <mSongList.size() ; i++) {
            if (mSongList.get(i).getPath().equals(path)){
                return mSongList.get(i);
            }
        }
        return null;
    }

    public void playMusic() throws IOException {
        if (mMediaPlayer!=null){
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(mContext,mCurrentSong.getUri());
        mMediaPlayer.prepare();
        mMediaPlayer.start();

    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public List<Album> getAllAlbum(){
        List<Album> albumList = new ArrayList<>();

        for (int i = 0; i < mSongList.size() ; i++) {
            boolean IsExist = false;
           check: for (int j = 0; j <albumList.size() ; j++) {
                if (albumList.get(j).getAlbumName().equals(mSongList.get(i).getAlbumName())){
                    IsExist=true;
                    break check;
                }
            }
            if (!IsExist){
                List<String> tempAlbum = new ArrayList<>();
                for (int j = 0; j < mSongList.size() ; j++) {
                    if (mSongList.get(j).getAlbumName().equals(mSongList.get(i).getAlbumName())){
                        tempAlbum.add(mSongList.get(j).getPath());
                    }
                }
                albumList.add(new Album(tempAlbum,mSongList.get(i).getAlbumName()));
            }

        }
        return albumList;
    }

    public void playNextMusic() throws IOException {
        for (int i = 0; i <mSongList.size() ; i++) {
            if (mSongList.get(i).getUUID().equals(mCurrentSong.getUUID())){
                setCurrentSong(mSongList.get(i+1));
                playMusic();
                return;
            }
        }
    }

    public void playPreviousMusic() throws IOException {
        for (int i = 0; i <mSongList.size() ; i++) {
            if (mSongList.get(i).getUUID().equals(mCurrentSong.getUUID())){
                setCurrentSong(mSongList.get(i-1));
                playMusic();
                return;
            }
        }
    }

    public List<Artist> getAllArtist(){
        List<Artist> artistList = new ArrayList<>();
        for (int i = 0; i < mSongList.size(); i++) {
           boolean IsExitArtist = false;
            check:for (int j = 0; j < artistList.size() ; j++) {
                if (artistList.get(j).getArtistName().equals(mSongList.get(i).getArtistName())){
                    IsExitArtist=true;
                   break check;
                }
            }
           if (!IsExitArtist){
               List<String> tempSong= new ArrayList<>();
               for (int j = 0; j < mSongList.size() ; j++) {
                   if (mSongList.get(j).getArtistName().equals(mSongList.get(i).getArtistName())){
                       tempSong.add(mSongList.get(j).getPath());
                   }
               }
               artistList.add(new Artist(tempSong,mSongList.get(i).getArtistName()));
           }
        }


        return artistList;
    }

    public void stopMusic(){
        mMediaPlayer.pause();
        mCurrentSecondOfMusic = mMediaPlayer.getCurrentPosition();
    }

    public void resumeMusic(){
        mMediaPlayer.seekTo(mCurrentSecondOfMusic);
        mMediaPlayer.start();
    }

    public Song getCurrentSong() {
        return mCurrentSong;
    }

    public void setCurrentSong(Song currentSong) {
        mCurrentSong = currentSong;
    }
}
