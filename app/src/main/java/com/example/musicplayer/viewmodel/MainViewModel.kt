package com.example.musicplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Observer // Required for observing LiveData
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.graphics.Bitmap // For artwork
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever // To fetch artwork if not in MediaMetadataCompat
import com.example.musicplayer.R
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Artist
import com.example.musicplayer.model.Folder
import com.example.musicplayer.model.Song
import com.example.musicplayer.repository.SongRepository
import com.example.musicplayer.service.MusicService // Required for service interaction
import kotlinx.coroutines.launch

// AndroidViewModel provides Application context
class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository: SongRepository = SongRepository.getInstance(app.applicationContext)
    private var musicService: MusicService? = null
    private var mediaController: MediaControllerCompat? = null
    private var mediaControllerCallback: MediaControllerCompat.Callback? = null

    // Data LiveData
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>> = _artists

    private val _folders = MutableLiveData<List<Folder>>()
    val folders: LiveData<List<Folder>> = _folders

    // Playback State LiveData (to be connected to MusicService)
    // These mirror state from the service.
    private val _currentTrackTitle = MutableLiveData<String?>().apply { value = "No Title" }
    val currentTrackTitle: LiveData<String?> = _currentTrackTitle

    private val _currentTrackArtist = MutableLiveData<String?>().apply { value = "Unknown Artist" }
    val currentTrackArtist: LiveData<String?> = _currentTrackArtist

    private val _currentTrackAlbum = MutableLiveData<String?>().apply { value = "Unknown Album" }
    val currentTrackAlbum: LiveData<String?> = _currentTrackAlbum

    private val _currentSongArtworkBitmap = MutableLiveData<Bitmap?>()
    val currentSongArtworkBitmap: LiveData<Bitmap?> = _currentSongArtworkBitmap

    private val _isTrackPlaying = MutableLiveData<Boolean>().apply { value = false }
    val isTrackPlaying: LiveData<Boolean> = _isTrackPlaying

    private val _currentSongDurationMs = MutableLiveData<Long>().apply { value = 0L }
    val currentSongDurationMs: LiveData<Long> = _currentSongDurationMs

    private val _currentPlaybackPositionMs = MutableLiveData<Long>().apply { value = 0L }
    val currentPlaybackPositionMs: LiveData<Long> = _currentPlaybackPositionMs

    private val _playbackErrorMessage = MutableLiveData<String?>()
    val playbackErrorMessage: LiveData<String?> = _playbackErrorMessage


    // Observer for MusicService LiveData
    private var servicePlaybackStateObserver: Observer<PlaybackStateCompat?>? = null
    private var serviceMetadataObserver: Observer<MediaMetadataCompat?>? = null
    private var serviceIsPlayingObserver: Observer<Boolean>? = null


    init {
        loadAllSongs()
        loadAllAlbums()
        loadAllArtists()
        loadAllFolders()
    }

    fun setMusicService(service: MusicService?) {
        musicService = service
        if (service != null) {
            mediaController = MediaControllerCompat(app, service.getMediaSessionToken())
            registerMediaControllerCallbacks()
            observeServiceLiveData()
            // Initial sync of state
            mediaController?.playbackState?.let { updateLocalPlaybackState(it) }
            mediaController?.metadata?.let { updateLocalMediaMetadata(it) }
        } else {
            unregisterMediaControllerCallbacks()
            removeServiceLiveDataObservers()
            mediaController = null
            // Optionally reset LiveData to default/empty states
            _isTrackPlaying.postValue(false)
            _currentTrackTitle.postValue("No Title")
            // ... reset others
        }
    }

    private fun registerMediaControllerCallbacks() {
        if (mediaControllerCallback != null) { // Unregister previous if any
           unregisterMediaControllerCallbacks()
        }
        mediaControllerCallback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                updateLocalPlaybackState(state)
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                updateLocalMediaMetadata(metadata)
            }
        }.also { mediaController?.registerCallback(it) }
    }

    private fun unregisterMediaControllerCallbacks() {
        mediaControllerCallback?.let { mediaController?.unregisterCallback(it) }
        mediaControllerCallback = null
    }

    private fun observeServiceLiveData() {
        musicService?.let { service ->
            // Remove any existing observers before adding new ones to prevent leaks/duplicates
            removeServiceLiveDataObservers()

            servicePlaybackStateObserver = Observer<PlaybackStateCompat?> { state ->
                updateLocalPlaybackState(state)
            }.also { service.playbackState.observeForever(it) } // Use observeForever as ViewModel outlives Activity/Fragment lifecycle sometimes

            serviceMetadataObserver = Observer<MediaMetadataCompat?> { metadata ->
                updateLocalMediaMetadata(metadata)
            }.also { service.currentPlayingSong.observeForever(it) }

            serviceIsPlayingObserver = Observer<Boolean> { isPlaying ->
                 _isTrackPlaying.postValue(isPlaying)
            }.also { service.isPlayingLiveData.observeForever(it) }
        }
    }

    private fun removeServiceLiveDataObservers() {
        servicePlaybackStateObserver?.let { musicService?.playbackState?.removeObserver(it) }
        serviceMetadataObserver?.let { musicService?.currentPlayingSong?.removeObserver(it) }
        serviceIsPlayingObserver?.let { musicService?.isPlayingLiveData?.removeObserver(it) }
    }

    private fun updateLocalPlaybackState(state: PlaybackStateCompat?) {
        _isTrackPlaying.postValue(state?.state == PlaybackStateCompat.STATE_PLAYING || state?.state == PlaybackStateCompat.STATE_BUFFERING)
        _currentPlaybackPositionMs.postValue(state?.position ?: 0L)
        if (state?.state == PlaybackStateCompat.STATE_ERROR) {
            _playbackErrorMessage.postValue(state.errorMessage?.toString() ?: "An unknown playback error occurred.")
        } else {
            _playbackErrorMessage.postValue(null) // Clear error
        }
    }

    private fun updateLocalMediaMetadata(metadata: MediaMetadataCompat?) {
        _currentTrackTitle.postValue(metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
        _currentTrackArtist.postValue(metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
        _currentTrackAlbum.postValue(metadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
        _currentSongDurationMs.postValue(metadata?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) ?: 0L)

        val artBitmap = metadata?.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)
        if (artBitmap != null) {
            _currentSongArtworkBitmap.postValue(artBitmap)
        } else {
            // Attempt to load artwork from URI if available (or path for local files)
            val artUri = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
            if (artUri != null) {
                // Implement logic to load Bitmap from URI (e.g., using Glide/Picasso or manually)
                // For simplicity, direct loading isn't done here to avoid Context issues in ViewModel.
                // This could also be a path to a local file if METADATA_KEY_MEDIA_ID is the path
                val songPath = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                songPath?.let { loadArtworkFromPath(it) }
            } else {
                 val songPath = metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                 songPath?.let { loadArtworkFromPath(it) } ?: _currentSongArtworkBitmap.postValue(null) // Fallback or clear
            }
        }
    }

    private fun loadArtworkFromPath(path: String) {
        viewModelScope.launch {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(path)
                val artBytes = retriever.embeddedPicture
                retriever.release()
                if (artBytes != null) {
                    _currentSongArtworkBitmap.postValue(BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size))
                } else {
                    _currentSongArtworkBitmap.postValue(null) // Or a default placeholder
                }
            } catch (e: Exception) {
                _currentSongArtworkBitmap.postValue(null) // Error loading artwork
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        removeServiceLiveDataObservers()
        unregisterMediaControllerCallbacks()
        // musicService reference is cleared by MainActivity's onStop/onServiceDisconnected
    }

    fun loadAllSongs() {
        viewModelScope.launch {
            _songs.postValue(repository.songList) // songList is already loaded in repo
        }
    }

    fun loadAllAlbums() {
        viewModelScope.launch {
            _albums.postValue(repository.getAllAlbum())
        }
    }

    fun loadAllArtists() {
        viewModelScope.launch {
            _artists.postValue(repository.getAllArtist())
        }
    }

    fun loadAllFolders() {
        viewModelScope.launch {
            _folders.postValue(repository.getAllFolder())
        }
    }

    // --- Playback Control Methods (will send commands to MusicService) ---

    fun playSong(songPath: String) {
        mediaController?.transportControls?.playFromMediaId(songPath, null)
    }

    // Overload for Song object, useful if called from UI with a Song instance
    fun playSong(song: Song) {
         mediaController?.transportControls?.playFromMediaId(song.path, null)
    }

    fun togglePlayPause() {
        val state = mediaController?.playbackState?.state
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING) {
            mediaController?.transportControls?.pause()
        } else {
            mediaController?.transportControls?.play()
        }
    }

    fun skipNext() {
        mediaController?.transportControls?.skipToNext()
    }

    fun skipPrevious() {
        mediaController?.transportControls?.skipToPrevious()
    }

    fun seekTo(position: Long) {
        mediaController?.transportControls?.seekTo(position)
    }
}
