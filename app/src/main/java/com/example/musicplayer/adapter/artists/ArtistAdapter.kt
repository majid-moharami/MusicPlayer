package com.example.musicplayer.adapter.artists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.model.Artist // Ensure this is the Kotlin data class
import com.example.musicplayer.model.Song // Ensure this is the Kotlin data class
import com.example.musicplayer.repository.SongRepository
import com.makeramen.roundedimageview.RoundedImageView

// Callback lambda for item clicks
typealias OnArtistClickListener = (Artist) -> Unit

class ArtistAdapter(
    private val context: Context, // For Glide and SongRepository
    private val onArtistClickListener: OnArtistClickListener
) : ListAdapter<Artist, ArtistAdapter.ArtistHolder>(ArtistDiffCallback()) {

    private val songRepository: SongRepository = SongRepository.getInstance(context.applicationContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.artist_list_item, parent, false)
        return ArtistHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistHolder, position: Int) {
        val artist = getItem(position)
        holder.bindArtist(artist)
    }

    inner class ArtistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverArtist: RoundedImageView = itemView.findViewById(R.id.cover_artist)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artist_name_artist_item)
        private val trackCountTextView: TextView = itemView.findViewById(R.id.track_count)
        private lateinit var currentArtist: Artist

        init {
            itemView.setOnClickListener {
                if (::currentArtist.isInitialized) {
                    onArtistClickListener(currentArtist)
                    // Logic for setting MusicRole, Priority should be in ViewModel/Service
                    // songRepository.isMain = false
                    // songRepository.musicRole = PlayMusicRole.ARTIST
                }
            }
        }

        fun bindArtist(artist: Artist) {
            currentArtist = artist
            artistNameTextView.text = artist.artistName
            trackCountTextView.text = "${artist.songsOfArtist?.size ?: 0} Track${if ((artist.songsOfArtist?.size ?: 0) > 1) "s" else ""}"

            // Similar to AlbumAdapter, load the artwork of the first song of the artist
            val firstSongPath = artist.songsOfArtist?.firstOrNull()
            var representativeSong: Song? = null
            if (firstSongPath != null) {
                representativeSong = songRepository.getSongFromPath(firstSongPath)
            }

            if (representativeSong != null) {
                Glide.with(itemView.context)
                    .load(representativeSong.path) // Assuming path can be loaded by Glide
                    .placeholder(R.mipmap.ic_empty_cover_foreground)
                    .error(R.mipmap.ic_empty_cover_foreground)
                    .into(coverArtist)
            } else {
                Glide.with(itemView.context)
                    .load(R.mipmap.ic_empty_cover_foreground) // Default if no song/path
                    .into(coverArtist)
            }
        }
    }
}

class ArtistDiffCallback : DiffUtil.ItemCallback<Artist>() {
    override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        // Assuming artistName is a unique identifier
        return oldItem.artistName == newItem.artistName
    }

    override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem == newItem // Data class equals()
    }
}
