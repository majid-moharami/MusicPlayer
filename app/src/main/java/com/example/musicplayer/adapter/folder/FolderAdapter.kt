package com.example.musicplayer.adapter.folder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.model.Folder // Ensure this is the Kotlin data class

// Callback lambda for item clicks
typealias OnFolderClickListener = (Folder) -> Unit

class FolderAdapter(
    private val context: Context, // Keep context if needed for resources, etc.
    private val onFolderClickListener: OnFolderClickListener
) : ListAdapter<Folder, FolderAdapter.FolderHolder>(FolderDiffCallback()) {

    // SongRepository might not be needed directly here if all interactions go through ViewModel
    // private val songRepository: SongRepository = SongRepository.getInstance(context.applicationContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_list_item, parent, false)
        return FolderHolder(view)
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        val folder = getItem(position)
        holder.bindFolder(folder)
    }

    inner class FolderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderImageView: ImageView = itemView.findViewById(R.id.imageView_folder)
        private val folderNameTextView: TextView = itemView.findViewById(R.id.textView_folder_name)
        private val trackCountTextView: TextView = itemView.findViewById(R.id.textView_track_count_folder)
        private lateinit var currentFolder: Folder

        init {
            itemView.setOnClickListener {
                if (::currentFolder.isInitialized) {
                    onFolderClickListener(currentFolder)
                    // Logic for setting MusicRole should be in ViewModel/Service
                    // Example:
                    // val songRepository = SongRepository.getInstance(itemView.context.applicationContext)
                    // songRepository.isMain = false
                    // songRepository.musicRole = PlayMusicRole.FOLDER
                }
            }
        }

        fun bindFolder(folder: Folder) {
            currentFolder = folder
            folderImageView.setImageResource(R.mipmap.ic_music_folder2_foreground) // Static folder icon
            folderNameTextView.text = folder.folderName
            trackCountTextView.text = "${folder.songsFolderPath.size} Track${if (folder.songsFolderPath.size > 1) "s" else ""}"
        }
    }
}

class FolderDiffCallback : DiffUtil.ItemCallback<Folder>() {
    override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        // Assuming folderName is unique enough for identity.
        // If folder paths were available and guaranteed unique, that would be better.
        return oldItem.folderName == newItem.folderName && oldItem.songsFolderPath.size == newItem.songsFolderPath.size
    }

    override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem == newItem // Data class equals()
    }
}
