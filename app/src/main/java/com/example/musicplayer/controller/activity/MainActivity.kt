package com.example.musicplayer.controller.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.musicplayer.R
import com.example.musicplayer.adapter.albums.AlbumAdapter
import com.example.musicplayer.adapter.artists.ArtistAdapter
import com.example.musicplayer.adapter.folder.FolderAdapter
import com.example.musicplayer.controller.fragment.AlbumListFragment
import com.example.musicplayer.controller.fragment.ArtistListFragment
import com.example.musicplayer.controller.fragment.FolderListFragment
import com.example.musicplayer.controller.fragment.SongsFragment
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.Artist
import com.example.musicplayer.model.Folder
import com.example.musicplayer.model.Song
import com.example.musicplayer.service.MusicService
import com.example.musicplayer.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.intentfilter.androidpermissions.PermissionManager
import com.intentfilter.androidpermissions.models.DeniedPermissions
import com.makeramen.roundedimageview.RoundedImageView
import java.util.Arrays
import java.util.UUID
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
    SongsFragment.InitNavigationPlayMusicCallback,
    ArtistAdapter.StartMusicListActivity,
    AlbumAdapter.StartMusicListActivityInAlbum,
    FolderAdapter.StartMusicListActivityInFolder {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toolbar: Toolbar
    private lateinit var includeLayout: View // TODO: Define more specific type if possible or remove if not used
    private lateinit var imageViewSongCover: RoundedImageView
    private lateinit var imageButtonNext: ImageView
    private lateinit var imageButtonPerv: ImageView // Previous button
    private lateinit var imageButtonPause: ImageView
    private lateinit var textViewArtist: TextView
    private lateinit var textViewSongName: TextView
    private lateinit var currentMusicPlayingView: View

    private val mainViewModel: MainViewModel by viewModels()
    private var musicService: MusicService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getLocalService()
            isServiceBound = true
            mainViewModel.setMusicService(musicService) // Notify ViewModel about service connection
            observeViewModel() // Start observing after service is connected and ViewModel is notified
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isServiceBound = false
            mainViewModel.setMusicService(null) // Notify ViewModel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        setSupportActionBar(toolbar)

        // Request permissions
        val permissionManager = PermissionManager.getInstance(this)
        permissionManager.checkPermissions(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), // Added READ for safety
            object : PermissionManager.PermissionRequestListener {
                override fun onPermissionGranted() {
                    // Permissions granted, proceed with UI initialization and data loading
                    mainViewModel.loadAllSongs() // Trigger data loading from ViewModel
                    mainViewModel.loadAllAlbums()
                    mainViewModel.loadAllArtists()
                    mainViewModel.loadAllFolders()
                    setupViewPagerAndTabs()
                    setupClickListeners()
                    // Initial UI state for play bar can be set here if needed, or observe ViewModel
                }

                override fun onPermissionDenied(deniedPermissions: DeniedPermissions) {
                    Toast.makeText(this@MainActivity, "Permissions Denied: ${Arrays.toString(deniedPermissions.toArray())}", Toast.LENGTH_LONG).show()
                    finish() // Close app if permissions are essential
                }
            })

        // No direct data loading or UI init here, moved to onPermissionGranted or observer
        // No savedInstanceState handling for playback here, ViewModel and Service will manage state
    }

    private fun findViews() {
        viewPager2 = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_bar)
        toolbar = findViewById(R.id.music_toolbar)
        includeLayout = findViewById(R.id.inclide_paly_bar) // Consider giving this a more specific ID if it's the whole bar
        imageViewSongCover = findViewById(R.id.cover_track)
        imageButtonNext = findViewById(R.id.next_button)
        imageButtonPerv = findViewById(R.id.per_button)
        imageButtonPause = findViewById(R.id.puse_button)
        textViewArtist = findViewById(R.id.artist_name)
        textViewSongName = findViewById(R.id.song_name)
        currentMusicPlayingView = findViewById(R.id.current_music_nav)
    }

    private fun setupViewPagerAndTabs() {
        viewPager2.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Songs"
                1 -> "Albums"
                2 -> "Artists"
                3 -> "Folders"
                4 -> "Favorite" // Assuming 5 tabs
                else -> null
            }
        }.attach()
    }


    private fun setupClickListeners() {
        currentMusicPlayingView.setOnClickListener {
            // Navigate to MusicSingleTrackActivity
            // Data for this activity will come from MainViewModel or SingleTrackPlayViewModel observing the service
            startActivityForResult(MusicSingleTrackActivity.newIntent(this@MainActivity), REQUEST_CODE_START_SINGLE_TRACK_ACTIVITY)
        }

        imageButtonPause.setOnClickListener {
            mainViewModel.togglePlayPause()
        }

        imageButtonNext.setOnClickListener {
            mainViewModel.skipNext()
        }

        imageButtonPerv.setOnClickListener { // "Perv" was likely a typo for "Prev"
            mainViewModel.skipPrevious()
        }
    }

    private fun observeViewModel() {
        if (!isServiceBound || musicService == null) return // Ensure service is bound

        mainViewModel.songs.observe(this, Observer { songs ->
            // Update ViewPager adapter for songs if necessary, or let fragment handle its own data via shared ViewModel
            // For now, fragments load their own data. If MainActivity needs to pass data, this is where it would happen.
        })
        // Similar observers for albums, artists, folders if MainActivity is responsible for passing data to fragments.
        // Currently, fragments seem to be self-sufficient or use their own ViewModel instances.

        mainViewModel.currentTrackTitle.observe(this, Observer { title ->
            textViewSongName.text = title ?: "Unknown Song"
        })

        mainViewModel.currentTrackArtist.observe(this, Observer { artist ->
            textViewArtist.text = artist ?: "Unknown Artist"
        })

        mainViewModel.isTrackPlaying.observe(this, Observer { isPlaying ->
            imageButtonPause.setImageResource(if (isPlaying) R.drawable.pause_round else R.drawable.play_round)
        })

        mainViewModel.currentSongArtworkBitmap.observe(this, Observer { artworkBitmap ->
             imageViewSongCover.setImageBitmap(artworkBitmap ?: BitmapFactory.decodeResource(resources, R.mipmap.ic_empty_cover_foreground))
        })

        mainViewModel.playbackErrorMessage.observe(this, Observer { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        })
    }


    override fun onStart() {
        super.onStart()
        // Bind to MusicService
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        // Unbind from MusicService
        if (isServiceBound) {
            mainViewModel.setMusicService(null) // Clear service instance in ViewModel
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    // Called from SongsFragment when a song is clicked
    override fun onClick(songPath: String) { // Changed from UUID to path, as playFromMediaId uses path
        mainViewModel.playSong(songPath)
    }

    // Callbacks from adapters - navigate to MusicListActivity
    override fun startForArtistCallBack(artist: Artist) {
        val intent = MusicListActivity.newIntent(this, null, artist, null)
        startActivityForResult(intent, REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_FOR_ARTIST)
    }

    override fun startForAlbumCallBack(album: Album) {
        val intent = MusicListActivity.newIntent(this, album, null, null)
        startActivityForResult(intent, REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_ALBUM)
    }

    override fun startForFolderCallBack(folder: Folder) {
        // Assuming REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_FOLDER would be more appropriate
        val intent = MusicListActivity.newIntent(this, null, null, folder)
        startActivityForResult(intent, REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_FOLDER) // Changed code
    }

    // ViewPager Adapter
    private class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 5 // Songs, Albums, Artists, Folders, Favorite

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SongsFragment.newInstance(null, null, null) // Assuming SongsFragment can handle nulls or uses ViewModel
                1 -> AlbumListFragment.newInstance()
                2 -> ArtistListFragment.newInstance()
                3 -> FolderListFragment.newInstance()
                4 -> SongsFragment.newInstance(null, null, null) // Placeholder for Favorite, using SongsFragment for now
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }

    companion object {
        const val REQUEST_CODE_START_SINGLE_TRACK_ACTIVITY = 1
        const val REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_FOR_ARTIST = 2
        const val REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_ALBUM = 3
        const val REQUEST_CODE_START_MUSIC_LIST_ACTIVITY_FOLDER = 4 // Added for clarity
    }

    // onActivityResult and onSaveInstanceState are simplified for now.
    // State restoration should primarily be handled by MusicService (for playback)
    // and ViewModels (for UI state).
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If MusicSingleTrackActivity or MusicListActivity modifies playback state,
        // MusicService's LiveData should automatically update MainActivity's UI via MainViewModel.
        // No explicit updateNavBar() needed if observers are set up correctly.
    }

    // onSaveInstanceState might not be needed for playback state if service handles it.
    // UI state related to ViewPager position etc. might still be relevant.
}
