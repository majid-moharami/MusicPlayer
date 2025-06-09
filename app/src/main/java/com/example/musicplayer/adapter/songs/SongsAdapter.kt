package com.example.musicplayer.adapter.songs

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever // Keep for direct artwork loading if Glide path fails
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
import com.example.musicplayer.model.Song // Ensure this is the Kotlin data class
import com.example.musicplayer.repository.SongRepository // May not be needed directly here anymore
import com.example.musicplayer.util.PlayMusicRole // These utils might be handled by ViewModel/Service
import com.example.musicplayer.util.PriorityOfSongsList

// Using (Song) -> Unit for the callback, passing the entire Song object
class SongsAdapter(
    private val context: Context, // Context for Glide
    private val playMusicCallback: (Song) -> Unit
) : ListAdapter<Song, SongsAdapter.SongHolder>(SongDiffCallback()) {

    // SongRepository might not be directly needed here if all interactions go through ViewModel
    // private val songRepository: SongRepository = SongRepository.getInstance(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item_list, parent, false)
        return SongHolder(view)
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val song = getItem(position)
        holder.bindSong(song)
    }

    inner class SongHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songCoverImage: ImageView = itemView.findViewById(R.id.cover_track)
        private val songTitle: TextView = itemView.findViewById(R.id.song_name_list_item)
        private val songArtist: TextView = itemView.findViewById(R.id.artist_name_list_item)
        private lateinit var currentSong: Song

        init {
            itemView.setOnClickListener {
                if (::currentSong.isInitialized) { // Check if currentSong has been initialized
                    playMusicCallback(currentSong)
                    // The logic for setting MusicRole and Priority should ideally be handled
                    // by the ViewModel or MusicService when a song starts playing,
                    // not directly in the adapter.
                    // Example:
                    // val songRepository = SongRepository.getInstance(itemView.context.applicationContext)
                    // if (songRepository.isMain) { // isMain property needs to be accessible
                    //     songRepository.musicRole = PlayMusicRole.ALL
                    //     songRepository.priority = PriorityOfSongsList.ALL
                    // } else {
                    //     when (songRepository.musicRole) {
                    //         PlayMusicRole.ALBUM -> songRepository.priority = PriorityOfSongsList.ALBUM
                    //         PlayMusicRole.ARTIST -> songRepository.priority = PriorityOfSongsList.ARTIST
                    //         PlayMusicRole.FOLDER -> songRepository.priority = PriorityOfSongsList.FOLDER
                    //         else -> {} // Handle other cases or default
                    //     }
                    // }
                }
            }
        }

        fun bindSong(song: Song) {
            currentSong = song
            songTitle.text = song.songName
            songArtist.text = song.artistName

            // Use Glide to load the image
            Glide.with(itemView.context)
                .load(song.path) // Using path; ensure this is a valid model for Glide (file path or URI string)
                .placeholder(R.mipmap.ic_empty_cover_foreground)
                .error(R.mipmap.ic_empty_cover_foreground) // Fallback image in case of error
                .into(songCoverImage)

            // Fallback or direct loading if Glide fails or path is not directly loadable by Glide
            // This might be an alternative if song.path isn't a direct image URL/URI for Glide
            // For local files, Glide typically handles file paths well.
            // If issues persist, ensure song.path is correct or use song.uri.
            // The original code had a complex way to load bitmap, Glide should simplify this.
            // If direct path loading with Glide is problematic, consider using song.uri or
            // loading bitmap manually and then using Glide to load the Bitmap.
        }
    }
}

class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.uuid == newItem.uuid // Comparing by UUID
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem // Data class equals() compares all properties
    }
}
