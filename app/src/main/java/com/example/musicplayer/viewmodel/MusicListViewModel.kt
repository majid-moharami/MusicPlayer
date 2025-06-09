package com.example.musicplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.model.Song
import com.example.musicplayer.repository.SongRepository
import kotlinx.coroutines.launch

class MusicListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SongRepository = SongRepository.getInstance(application.applicationContext)

    // Example LiveData for a list of songs (could be all songs, or songs from a specific album/artist/folder)
    private val _songsInList = MutableLiveData<List<Song>>()
    val songsInList: LiveData<List<Song>> = _songsInList

    // TODO: Add methods to load specific lists, e.g., by albumId, artistId, folderPath
    // These would interact with SongRepository

    fun loadAllSongs() { // Example: reuse or adapt from MainViewModel or make more specific
        viewModelScope.launch {
            _songsInList.postValue(repository.songList)
        }
    }

    // TODO: Add interaction methods, e.g., what happens when a song in the list is clicked.
    // This would typically involve communicating with the MainViewModel or directly with the MusicService.
    fun onSongSelected(song: Song) {
        // Placeholder: This might tell a shared ViewModel or a service to play this song.
        // For example, by calling a method on MainViewModel or sending a command to MusicService.
    }
}
