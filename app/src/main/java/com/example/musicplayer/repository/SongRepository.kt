package com.example.musicplayer.repository

import android.annotation.SuppressLint
import android.content.Context
import com.example.musicplayer.manager.SharedPreferencesMusic
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Artist
import com.example.musicplayer.model.Folder
import com.example.musicplayer.model.Song
import com.example.musicplayer.util.FolderSeparator
import com.example.musicplayer.util.MusicState
import com.example.musicplayer.util.PlayMusicRole
import com.example.musicplayer.util.PriorityOfSongsList
import com.example.musicplayer.util.SongLoader
import java.util.UUID

class SongRepository private constructor() {

    val songList: List<Song> = SongLoader.loadAllSong(context)
    var currentArtistSongs: List<Song> = ArrayList()
    var currentAlbumSongs: List<Song> = ArrayList()
    var currentFolderSongs: List<Song> = ArrayList()

    var currentSong: Song? = null
    var isMain: Boolean = true // TODO: Re-evaluate necessity
    var shuffleState: MusicState = MusicState.IS_NOT_SHUFFLE // TODO: Move to MusicService/ViewModel
    var repeatState: MusicState = MusicState.IS_NOT_REPEAT // TODO: Move to MusicService/ViewModel
    var musicRole: PlayMusicRole = PlayMusicRole.ALL // TODO: Re-evaluate or move
    var priority: PriorityOfSongsList = PriorityOfSongsList.ALL // TODO: Re-evaluate or move

    // Removed: mMediaPlayer, mCurrentSecondOfMusic

    fun getSong(uuid: UUID): Song? {
        return songList.find { it.uuid == uuid }
    }

    fun getSongFromPath(path: String): Song? {
        return songList.find { it.path == path }
    }

    fun getPosition(song: Song): Int {
        return songList.indexOfFirst { it.uuid == song.uuid }
    }

    fun getAllAlbum(): List<Album> {
        val albumList = mutableListOf<Album>()
        songList.forEach { song ->
            if (albumList.none { album -> album.albumName == song.albumName }) {
                val tempAlbumSongs = songList.filter { s -> s.albumName == song.albumName }.map { it.path }
                albumList.add(Album(tempAlbumSongs.toMutableList(), song.albumName))
            }
        }
        return albumList
    }

    fun getAllFolder(): List<Folder> {
        return FolderSeparator.getFolders(songList)
    }

    fun getAllArtist(): List<Artist> {
        val artistList = mutableListOf<Artist>()
        songList.forEach { song ->
            if (artistList.none { artist -> artist.artistName == song.artistName }) {
                val tempArtistSongs = songList.filter { s -> s.artistName == song.artistName }.map { it.path }
                artistList.add(Artist(tempArtistSongs.toMutableList(), song.artistName))
            }
        }
        return artistList
    }

    companion object {
        @SuppressLint("StaticFieldLeak") // Context is application context, should be fine.
        private lateinit var context: Context
        private var sSongRepository: SongRepository? = null
        private lateinit var sharedPreferencesMusic: SharedPreferencesMusic // TODO: Evaluate usage

        fun getInstance(applicationContext: Context): SongRepository {
            context = applicationContext.applicationContext // Ensure application context
            if (sSongRepository == null) {
                sSongRepository = SongRepository()
                sharedPreferencesMusic = SharedPreferencesMusic(context) // Initialize SharedPreferences
            }
            return sSongRepository!!
        }
    }
}
