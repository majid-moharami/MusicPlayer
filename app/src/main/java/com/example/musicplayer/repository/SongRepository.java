package com.example.musicplayer.repository;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Folder;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.util.FolderSeparator;
import com.example.musicplayer.util.MusicState;
import com.example.musicplayer.util.PlayMusicRole;
import com.example.musicplayer.util.PriorityOfSongsList;
import com.example.musicplayer.util.SongLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SongRepository {
    private static SongRepository sSongRepository;
    private static Context mContext;
    private List<Song> mSongList;
    private List<Song> mCurrentArtistSongs = new ArrayList<>();
    private List<Song> mCurrentAlbumSongs = new ArrayList<>();
    private List<Song> mCurrentFolderSongs = new ArrayList<>();
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Song mCurrentSong;
    private boolean mIsMain = true;
    private MusicState mShuffleState = MusicState.IS_NOT_SHUFFLE;
    private MusicState mRepeatState = MusicState.IS_NOT_REPEAT;
    private PlayMusicRole mMusicRole = PlayMusicRole.ALL;
    private PriorityOfSongsList mPriority = PriorityOfSongsList.ALL;

    private int mCurrentSecondOfMusic;


    public static SongRepository getSongRepository(Context context) {
        mContext = context.getApplicationContext();
        if (sSongRepository == null)
            sSongRepository = new SongRepository();

        return sSongRepository;
    }

    private SongRepository() {
        mSongList = SongLoader.loadAllSong(mContext);
    }

    public List<Song> getSongList() {
        return mSongList;
    }

    public Song getSong(UUID uuid) {
        for (int i = 0; i < mSongList.size(); i++) {
            if (mSongList.get(i).getUUID() == uuid) {
                return mSongList.get(i);
            }
        }
        return null;
    }

    public Song getSongFromPath(String path) {
        for (int i = 0; i < mSongList.size(); i++) {
            if (mSongList.get(i).getPath().equals(path)) {
                return mSongList.get(i);
            }
        }
        return null;
    }

    public void playMusic() throws IOException {
        if (mShuffleState == MusicState.IS_SHUFFLE) {
            if (mMusicRole == PlayMusicRole.ALBUM && mPriority == PriorityOfSongsList.ALBUM) {
                Random random = new Random();
                mCurrentSong = mCurrentAlbumSongs.get(random.nextInt(mCurrentAlbumSongs.size()));
            } else if (mMusicRole == PlayMusicRole.ARTIST && mPriority == PriorityOfSongsList.ARTIST) {
                Random random = new Random();
                mCurrentSong = mCurrentArtistSongs.get(random.nextInt(mCurrentArtistSongs.size()));
            } else if (mMusicRole == PlayMusicRole.FOLDER && mPriority == PriorityOfSongsList.FOLDER) {
                Random random = new Random();
                mCurrentSong = mCurrentFolderSongs.get(random.nextInt(mCurrentFolderSongs.size()));
            } else {
                Random random = new Random();
                mCurrentSong = mSongList.get(random.nextInt(mSongList.size()));
            }
        }
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(mContext, mCurrentSong.getUri());
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    public int getPosition(Song song) {
        for (int i = 0; i < mSongList.size(); i++) {
            if (mSongList.get(i).getUUID().equals(song.getUUID()))
                return i;
        }
        return -1;
    }

    public void playMusicWithoutShuffle() throws IOException {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(mContext, mCurrentSong.getUri());
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public List<Album> getAllAlbum() {
        List<Album> albumList = new ArrayList<>();

        for (int i = 0; i < mSongList.size(); i++) {
            boolean IsExist = false;
            check:
            for (int j = 0; j < albumList.size(); j++) {
                if (albumList.get(j).getAlbumName().equals(mSongList.get(i).getAlbumName())) {
                    IsExist = true;
                    break check;
                }
            }
            if (!IsExist) {
                List<String> tempAlbum = new ArrayList<>();
                for (int j = 0; j < mSongList.size(); j++) {
                    if (mSongList.get(j).getAlbumName().equals(mSongList.get(i).getAlbumName())) {
                        tempAlbum.add(mSongList.get(j).getPath());
                    }
                }
                albumList.add(new Album(tempAlbum, mSongList.get(i).getAlbumName()));
            }

        }
        return albumList;
    }

    public List<Folder> getAllFolder() {
        return FolderSeparator.getFolders(mSongList);
    }

    public void playNextMusic() throws IOException {
        if ( mPriority == PriorityOfSongsList.ALBUM) {
            for (int i = 0; i < mCurrentAlbumSongs.size(); i++) {
                if (mCurrentAlbumSongs.get(i).getUUID().equals(mCurrentSong.getUUID())) {
                    if (i != mCurrentAlbumSongs.size() - 1)
                        setCurrentSong(mCurrentAlbumSongs.get(i + 1));
                    else
                        setCurrentSong(mCurrentAlbumSongs.get(0));
                    playMusic();
                    return;
                }
            }
        } else if (mPriority == PriorityOfSongsList.ARTIST) {
            for (int i = 0; i < mCurrentArtistSongs.size(); i++) {
                if (mCurrentArtistSongs.get(i).getUUID().equals(mCurrentSong.getUUID())) {
                    if (i != mCurrentArtistSongs.size() - 1)
                        setCurrentSong(mCurrentArtistSongs.get(i + 1));
                    else
                        setCurrentSong(mCurrentArtistSongs.get(0));
                    playMusic();
                    return;
                }
            }
        } else if ( mPriority == PriorityOfSongsList.FOLDER) {
            for (int i = 0; i < mCurrentFolderSongs.size(); i++) {
                if (mCurrentFolderSongs.get(i).getUUID().equals(mCurrentSong.getUUID())) {
                    if (i != mCurrentFolderSongs.size() - 1)
                        setCurrentSong(mCurrentFolderSongs.get(i + 1));
                    else
                        setCurrentSong(mCurrentFolderSongs.get(0));
                    playMusic();
                    return;
                }
            }
        } else {
            for (int i = 0; i < mSongList.size(); i++) {
                if (mSongList.get(i).getUUID().equals(mCurrentSong.getUUID())) {
                    if (i != mSongList.size() - 1)
                        setCurrentSong(mSongList.get(i + 1));
                    else
                        setCurrentSong(mSongList.get(0));
                    playMusic();
                    return;
                }
            }
        }
    }

    public void playPreviousMusic() throws IOException {
        if (mMusicRole == PlayMusicRole.ALBUM && mPriority == PriorityOfSongsList.ALBUM) {
            for (int i = 0; i < mCurrentAlbumSongs.size(); i++) {
                if (mCurrentAlbumSongs.get(i).getUUID().equals(mCurrentSong.getUUID())) {
                    if (i != 0)
                        setCurrentSong(mCurrentAlbumSongs.get(i - 1));
                    else
                        setCurrentSong(mCurrentAlbumSongs.get(mCurrentAlbumSongs.size() - 1));

                    playMusic();
                    return;
                }
            }
        } else if (mMusicRole == PlayMusicRole.ARTIST && mPriority == PriorityOfSongsList.ARTIST) {
            for (int i = 0; i < mCurrentArtistSongs.size(); i++) {
                if (mCurrentArtistSongs.get(i).getUUID().equals(mCurrentSong.getUUID())) {
                    if (i != 0)
                        setCurrentSong(mCurrentArtistSongs.get(i - 1));
                    else
                        setCurrentSong(mCurrentArtistSongs.get(mCurrentArtistSongs.size() - 1));
                    playMusic();
                    return;
                }
            }
        } else if (mMusicRole == PlayMusicRole.FOLDER && mPriority == PriorityOfSongsList.FOLDER) {
            for (int i = 0; i < mCurrentFolderSongs.size(); i++) {
                if (mCurrentFolderSongs.get(i).getUUID().equals(mCurrentSong.getUUID())) {
                    if (i != 0)
                        setCurrentSong(mCurrentFolderSongs.get(i - 1));
                    else
                        setCurrentSong(mCurrentFolderSongs.get(mCurrentFolderSongs.size() - 1));
                    playMusic();
                    return;
                }
            }
        } else {
            for (int i = 0; i < mSongList.size(); i++) {
                if (mSongList.get(i).getUUID().equals(mCurrentSong.getUUID())) {
                    if (i != 0)
                        setCurrentSong(mSongList.get(i - 1));
                    else
                        setCurrentSong(mSongList.get(mSongList.size() - 1));
                    playMusic();
                    return;
                }
            }
        }
    }

    public List<Artist> getAllArtist() {
        List<Artist> artistList = new ArrayList<>();
        for (int i = 0; i < mSongList.size(); i++) {
            boolean IsExitArtist = false;
            check:
            for (int j = 0; j < artistList.size(); j++) {
                if (artistList.get(j).getArtistName().equals(mSongList.get(i).getArtistName())) {
                    IsExitArtist = true;
                    break check;
                }
            }
            if (!IsExitArtist) {
                List<String> tempSong = new ArrayList<>();
                for (int j = 0; j < mSongList.size(); j++) {
                    if (mSongList.get(j).getArtistName().equals(mSongList.get(i).getArtistName())) {
                        tempSong.add(mSongList.get(j).getPath());
                    }
                }
                artistList.add(new Artist(tempSong, mSongList.get(i).getArtistName()));
            }
        }


        return artistList;
    }

    public void stopMusic() {
        mMediaPlayer.pause();
        mCurrentSecondOfMusic = mMediaPlayer.getCurrentPosition();
    }

    public PriorityOfSongsList getPriority() {
        return mPriority;
    }

    public void setPriority(PriorityOfSongsList mPriority) {
        this.mPriority = mPriority;
    }

    public void resumeMusic() {
        mMediaPlayer.seekTo(mCurrentSecondOfMusic);
        mMediaPlayer.start();
    }

    public boolean isMain() {
        return mIsMain;
    }

    public void setIsMain(boolean mIsMain) {
        this.mIsMain = mIsMain;
    }

    public PlayMusicRole getMusicRole() {
        return mMusicRole;
    }

    public void setMusicRole(PlayMusicRole mMusicRole) {
        this.mMusicRole = mMusicRole;
    }

    public List<Song> getCurrentArtistSongs() {
        return mCurrentArtistSongs;
    }

    public void setCurrentArtistSongs(List<Song> mCurrentArtistSongs) {
        this.mCurrentArtistSongs = mCurrentArtistSongs;
    }

    public void setCurrentFolderSongs(List<Song> mCurrentFolderSongs) {
        this.mCurrentFolderSongs = mCurrentFolderSongs;
    }


    public List<Song> getCurrentAlbumSongs() {
        return mCurrentAlbumSongs;
    }

    public void setCurrentAlbumSongs(List<Song> mCurrentAlbumSongs) {
        this.mCurrentAlbumSongs = mCurrentAlbumSongs;
    }

    public Song getCurrentSong() {
        return mCurrentSong;
    }

    public void setCurrentSong(Song currentSong) {
        mCurrentSong = currentSong;
    }

    public MusicState getShuffleState() {
        return mShuffleState;
    }

    public void setShuffleState(MusicState mShuffleState) {
        this.mShuffleState = mShuffleState;
    }

    public MusicState getRepeatState() {
        return mRepeatState;
    }

    public void setRepeatState(MusicState mRepeatState) {
        this.mRepeatState = mRepeatState;
    }
}
