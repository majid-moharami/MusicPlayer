package com.example.musicplayer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver // Required for MediaButtonPendingIntent
import com.example.musicplayer.repository.SongRepository
import com.example.musicplayer.model.Song
import com.example.musicplayer.util.MusicNotification // Import MusicNotification
import java.io.IOException
import android.support.v4.media.MediaMetadataCompat // Required for MediaMetadata
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var repository: SongRepository
    private val binder = LocalBinder()

    private lateinit var mediaSession: MediaSessionCompat
    private var _currentSongInternal: Song? = null // Internal mutable song

    // LiveData to expose state
    private val _currentPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val currentPlayingSong: LiveData<MediaMetadataCompat?> = _currentPlayingSong

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlayingLiveData: LiveData<Boolean> = _isPlaying

    private var currentSecondOfMusic: Int = 0


    companion object {
        // Define action strings for MediaSession and Intents (can be reused)
        const val ACTION_PLAY = "com.example.musicplayer.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.musicplayer.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.musicplayer.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.musicplayer.ACTION_PREVIOUS"
        const val ACTION_STOP = "com.example.musicplayer.ACTION_STOP" // Example for stop action

        fun newIntent(context: Context): Intent {
            return Intent(context, MusicService::class.java)
        }
    }

    inner class LocalBinder : Binder() {
        fun getLocalService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        repository = SongRepository.getInstance(applicationContext)
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener {
                // Handle song completion, e.g., play next or stop
                mediaSessionCallback.onSkipToNext() // Or specific completion logic
            }
            // Add other listeners like setOnErrorListener if needed
        }

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, "MusicService")

        // Set an initial PlaybackState with ACTION_PLAY_PAUSE
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_STOP // Include stop if you handle it
            )
        mediaSession.setPlaybackState(stateBuilder.build())

        // MySessionCallback() has methods that handle callbacks from a media controller
        mediaSession.setCallback(mediaSessionCallback)

        // Set the session's token so that client activities can communicate with it.
        // sessionToken = mediaSession.sessionToken // Not directly used here, but good to know

        mediaSession.isActive = true
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            _currentSongInternal?.let { song ->
                if (!mediaPlayer.isPlaying) {
                    try {
                        // If resuming and currentSecondOfMusic has a valid pause point
                        if (mediaPlayer.currentPosition == 0 && currentSecondOfMusic > 0) { // Check if it's not already playing or at start
                            mediaPlayer.reset() // Reset only if necessary (e.g. new song or error)
                            mediaPlayer.setDataSource(this@MusicService, song.uri)
                            mediaPlayer.prepare()
                            mediaPlayer.seekTo(currentSecondOfMusic)
                        } else if (mediaPlayer.currentPosition == 0) { // Starting fresh or new song
                            mediaPlayer.reset()
                            mediaPlayer.setDataSource(this@MusicService, song.uri)
                            mediaPlayer.prepare()
                            currentSecondOfMusic = 0
                        }
                        mediaPlayer.start()
                        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        updatePlaybackState(PlaybackStateCompat.STATE_ERROR)
                    }
                }
            } ?: run {
                // No current song, try to play the first from the list or handle error
                if (repository.songList.isNotEmpty()) {
                    playSongInternal(repository.songList.first())
                } else {
                     updatePlaybackState(PlaybackStateCompat.STATE_NONE)
                }
            }
        }

        override fun onPause() {
            super.onPause()
            // Called when the MediaController wants to pause.
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                currentSecondOfMusic = mediaPlayer.currentPosition
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            // Called when the MediaController wants to skip to the next track.
            playNextSongInternal()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            // Called when the MediaController wants to skip to the previous track.
            playPreviousSongInternal()
        }

        override fun onStop() {
            super.onStop()
            // Called when the MediaController wants to stop.
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                // Calling prepare() after stop() is necessary if you want to play again from this MediaPlayer instance
                // For a full stop where resources are released or reset, you might do more.
                mediaPlayer.reset() // Resets the MediaPlayer to its uninitialized state.
                currentSecondOfMusic = 0
                updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
                stopForeground(false) // Allow notification to be dismissed
            } catch (e: Exception) {
                e.printStackTrace()
                updatePlaybackState(PlaybackStateCompat.STATE_ERROR)
            }
        }

        // You can also override onPlayFromMediaId, onPlayFromSearch, onPlayFromUri, etc.
        override fun onPlayFromMediaId(mediaId: String?, extras: android.os.Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            mediaId?.let { songPath ->
                val songToPlay = repository.getSongFromPath(songPath)
                if (songToPlay != null) {
                    _currentSongInternal = songToPlay
                    // Reset position for new song
                    currentSecondOfMusic = 0
                    // Update metadata before playing
                    updateMediaMetadata(_currentSongInternal)
                    onPlay()
                }
            }
        }
    }

    private fun updatePlaybackState(state: Int) {
        val currentPosition = if (mediaPlayer.isPlaying || state == PlaybackStateCompat.STATE_PAUSED) {
            mediaPlayer.currentPosition.toLong()
        } else {
            0L // Or PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
        }
        val playbackSpeed = if (state == PlaybackStateCompat.STATE_PLAYING) 1.0f else 0.0f

        val newState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_STOP
            )
            .setState(state, currentPosition, playbackSpeed)
            .build()

        mediaSession.setPlaybackState(newState)
        _playbackState.postValue(newState)
        _isPlaying.postValue(state == PlaybackStateCompat.STATE_PLAYING)

        // Update notification and MediaMetadata
        _currentSongInternal?.let { updateMediaMetadata(it) } // Update metadata for current song
        showNotification() // Always show/update notification with new state
    }

    private fun updateMediaMetadata(song: Song?) {
        song?.let {
            val duration = try {
                // Ensure media player is prepared to get duration
                if (mediaPlayer.isPlaying || mediaPlayer.currentPosition > 0) mediaPlayer.duration.toLong() else 0L
            } catch (e: IllegalStateException) {
                0L // MediaPlayer might not be in a valid state
            }

            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it.path) // Important for onPlayFromMediaId
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, it.songName)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, it.artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, it.albumName)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                // TODO: Add Album Art using METADATA_KEY_ALBUM_ART_URI or METADATA_KEY_ALBUM_ART
                .build()
            mediaSession.setMetadata(metadata)
            _currentPlayingSong.postValue(metadata)
        } ?: run {
            // No song, clear metadata
            mediaSession.setMetadata(null)
            _currentPlayingSong.postValue(null)
        }
    }

    private fun showNotification() {
        _currentSongInternal?.let { song ->
            val notification = MusicNotification.createNotification(
                this,
                mediaSession.sessionToken,
                song,
                _isPlaying.value ?: false // Use LiveData value
            )
            startForeground(MusicNotification.NOTIFICATION_ID, notification)
        } ?: run {
             // If no current song, perhaps stop foreground or show a default state notification
            stopForeground(true) // Example: remove notification if nothing is loaded
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Route media button events to MediaSession
        MediaButtonReceiver.handleIntent(mediaSession, intent)

        // Handle explicit actions sent to the service (e.g., from notifications not using MediaButtonReceiver directly)
        // Or if you have custom actions.
        intent?.action?.let { action ->
            when (action) {
                ACTION_PLAY -> mediaSessionCallback.onPlay() // Or directly call play logic if appropriate
                ACTION_PAUSE -> mediaSessionCallback.onPause()
                ACTION_NEXT -> mediaSessionCallback.onSkipToNext()
                ACTION_PREVIOUS -> mediaSessionCallback.onSkipToPrevious()
                ACTION_STOP -> mediaSessionCallback.onStop()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mediaSession.release()
    }

    // --- Internal Playback Methods (to be called by MediaSession Callbacks) ---
    private fun playSongInternal(song: Song?) {
        song?.let {
            _currentSongInternal = it // Update internal current song
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(this, it.uri)
                mediaPlayer.prepare()
                // mediaPlayer.start() will be called by onPlay if needed or directly if auto-play
                currentSecondOfMusic = 0 // Reset for new song
                // Update metadata and initial playback state (usually paused until onPlay is called)
                updateMediaMetadata(it)
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED) // Start in paused, onPlay will change it

                // If auto-play is desired after selecting a song, call onPlay here or ensure MediaController calls it
                // For example, if playFromMediaId should auto-play:
                // mediaSessionCallback.onPlay()

            } catch (e: IOException) {
                e.printStackTrace()
                updatePlaybackState(PlaybackStateCompat.STATE_ERROR)
            }
        } ?: run {
            updatePlaybackState(PlaybackStateCompat.STATE_NONE)
        }
    }

    private fun playNextSongInternal() {
        val originalSongPath = _currentSongInternal?.path
        var newSongToPlay: Song? = null
        if (repository.songList.isNotEmpty()) {
            if (_currentSongInternal == null) {
                newSongToPlay = repository.songList.first()
            } else {
                val currentIndex = repository.getPosition(_currentSongInternal!!)
                newSongToPlay = if (currentIndex != -1 && currentIndex < repository.songList.size - 1) {
                    repository.songList[currentIndex + 1]
                } else {
                    repository.songList.first() // Loop back
                }
            }
        }

        if (newSongToPlay != null) {
            // repository.currentSong = newSongToPlay // Let service manage current song
            _currentSongInternal = newSongToPlay
            currentSecondOfMusic = 0 // Reset for new song
            updateMediaMetadata(newSongToPlay) // Update metadata first
            mediaSessionCallback.onPlay()      // Then trigger play
        }
    }

    private fun playPreviousSongInternal() {
        val originalSongPath = _currentSongInternal?.path
        var newSongToPlay: Song? = null
        if (repository.songList.isNotEmpty()) {
            if (_currentSongInternal == null) {
                newSongToPlay = repository.songList.last()
            } else {
                val currentIndex = repository.getPosition(_currentSongInternal!!)
                newSongToPlay = if (currentIndex != -1 && currentIndex > 0) {
                    repository.songList[currentIndex - 1]
                } else {
                    repository.songList.last() // Loop back
                }
            }
        }
        if (newSongToPlay != null) {
            // repository.currentSong = newSongToPlay
            _currentSongInternal = newSongToPlay
            currentSecondOfMusic = 0
            updateMediaMetadata(newSongToPlay)
            mediaSessionCallback.onPlay()
        }
    }

    // --- Public methods that might be called by UI (via Binder) ---
    // These should ideally trigger MediaSessionCompat actions rather than direct control

    // Expose LiveData via Binder for more direct observation if needed by client,
    // though ViewModels should ideally be the primary consumers.
    fun getCurrentPlayingSongLiveData(): LiveData<MediaMetadataCompat?> = currentPlayingSong
    fun getPlaybackStateLiveData(): LiveData<PlaybackStateCompat?> = playbackState
    fun getIsPlayingLiveData(): LiveData<Boolean> = isPlayingLiveData


    // Example: If UI needs to directly tell service to play a specific song by path
    fun playSongByPath(path: String) {
        val songToPlay = repository.getSongFromPath(path)
        if (songToPlay != null) {
            // Update internal song state first
            _currentSongInternal = songToPlay
            currentSecondOfMusic = 0 // Reset position for the new song

            // Trigger MediaSession to play this song
            // This will call onPlayFromMediaId in the callback
            mediaSession.controller.transportControls.playFromMediaId(path, null)
        }
    }

    fun getMediaSessionToken(): MediaSessionCompat.Token {
        return mediaSession.sessionToken
    }
}
