package com.example.musicplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicplayer.model.Song // Assuming you might want full Song object details
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat

// This ViewModel could be used by an Activity/Fragment that displays the currently playing track
// and provides playback controls. It would heavily rely on observing state from MainViewModel or MusicService.
class SingleTrackPlayViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for UI elements specific to the single track view
    private val _trackTitle = MutableLiveData<String?>()
    val trackTitle: LiveData<String?> = _trackTitle

    private val _trackArtist = MutableLiveData<String?>()
    val trackArtist: LiveData<String?> = _trackArtist

    private val _trackAlbum = MutableLiveData<String?>()
    val trackAlbum: LiveData<String?> = _trackAlbum

    private val _albumArtUri = MutableLiveData<String?>() // Or Bitmap, depending on how you handle images
    val albumArtUri: LiveData<String?> = _albumArtUri

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _duration = MutableLiveData<Long>() // Song duration in ms
    val duration: LiveData<Long> = _duration

    private val _currentPosition = MutableLiveData<Long>() // Current playback position in ms
    val currentPosition: LiveData<Long> = _currentPosition

    // TODO: This ViewModel would observe LiveData from MainViewModel or directly from MusicService
    // (if a direct connection from Activity/Fragment to Service's LiveData is preferred over piping through MainViewModel).

    // Example of how it might update its state based on observed data from another source (e.g., MainViewModel)
    fun updateFromMediaMetadata(metadata: MediaMetadataCompat?) {
        _trackTitle.value = metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        _trackArtist.value = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        _trackAlbum.value = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
        _albumArtUri.value = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
        _duration.value = metadata?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
    }

    fun updateFromPlaybackState(state: PlaybackStateCompat?) {
        _isPlaying.value = state?.state == PlaybackStateCompat.STATE_PLAYING ||
                           state?.state == PlaybackStateCompat.STATE_BUFFERING
        _currentPosition.value = state?.position
    }

    // --- Playback Control Methods (would delegate to MainViewModel or MusicService) ---
    fun playPause() {
        // TODO: Delegate to MainViewModel.togglePlayPause() or send command to service
    }

    fun skipNext() {
        // TODO: Delegate to MainViewModel.skipNext() or send command to service
    }

    fun skipPrevious() {
        // TODO: Delegate to MainViewModel.skipPrevious() or send command to service
    }

    fun seekTo(position: Long) {
        // TODO: Delegate to MainViewModel.seekTo(position) or send command to service
    }
}
