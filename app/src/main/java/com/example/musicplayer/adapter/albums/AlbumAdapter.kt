package com.example.musicplayer.adapter.albums

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.model.Album // Ensure this is the Kotlin data class
import com.example.musicplayer.model.Song // Ensure this is the Kotlin data class
import com.example.musicplayer.repository.SongRepository // May be needed to get artist name

// Callback lambda for item clicks
typealias OnAlbumClickListener = (Album) -> Unit

class AlbumAdapter(
    private val context: Context, // For Glide and SongRepository instantiation
    private val onAlbumClickListener: OnAlbumClickListener
) : ListAdapter<Album, AlbumAdapter.AlbumHolder>(AlbumDiffCallback()) {

    // SongRepository might be needed if album art or artist info isn't directly in Album model
    // Or if this information needs to be fetched based on a song from the album.
    private val songRepository: SongRepository = SongRepository.getInstance(context.applicationContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_list_item, parent, false)
        return AlbumHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        val album = getItem(position)
        holder.bindAlbum(album)
    }

    inner class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverAlbum: ImageView = itemView.findViewById(R.id.album_cover)
        private val albumNameTextView: TextView = itemView.findViewById(R.id.album_name)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artist_name_in_album_list)
        private lateinit var currentAlbum: Album

        init {
            itemView.setOnClickListener {
                if (::currentAlbum.isInitialized) {
                    onAlbumClickListener(currentAlbum)
                    // Logic for setting MusicRole, Priority should be in ViewModel/Service
                    // Example:
                    // songRepository.isMain = false
                    // songRepository.musicRole = PlayMusicRole.ALBUM
                }
            }
        }

        fun bindAlbum(album: Album) {
            currentAlbum = album
            albumNameTextView.text = album.albumName

            // The original adapter fetches the first song to get artist name and cover.
            // This assumes songsOfAlbum is a list of song paths or IDs.
            // If songsOfAlbum is empty, this will crash or show default.
            val firstSongPath = album.songsOfAlbum?.firstOrNull()
            var representativeSong: Song? = null
            if (firstSongPath != null) {
                representativeSong = songRepository.getSongFromPath(firstSongPath)
            }

            if (representativeSong != null) {
                artistNameTextView.text = "${representativeSong.artistName} | ${album.songsOfAlbum?.size ?: 0} songs"
                Glide.with(itemView.context)
                    .load(representativeSong.path) // Assuming path can be loaded by Glide for album art
                    .placeholder(R.mipmap.ic_empty_cover_foreground)
                    .error(R.mipmap.ic_empty_cover_foreground)
                    .into(coverAlbum)
            } else {
                artistNameTextView.text = "Unknown Artist | ${album.songsOfAlbum?.size ?: 0} songs"
                Glide.with(itemView.context)
                    .load(R.mipmap.ic_empty_cover_foreground) // Default if no song/path
                    .into(coverAlbum)
            }
        }
    }
}

class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        // Assuming albumName is a unique identifier for an Album
        return oldItem.albumName == newItem.albumName
    }

    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem // Data class equals()
    }
}
