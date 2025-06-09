package com.example.musicplayer.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.musicplayer.model.Song // Ensure this is the Kotlin data class

fun loadAllSong(context: Context): List<Song> {
    val tempAudioList = mutableListOf<Song>()
    val songUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DATA, // Path
        MediaStore.Audio.Media.DURATION // Optional: if you want duration
    )

    // Querying MediaStore
    val songCursor: Cursor? = context.contentResolver.query(
        songUri,
        projection,
        null, // Could add selection here, e.g., MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        null,
        MediaStore.Audio.Media.TITLE + " ASC" // Sort order
    )

    songCursor?.use { cursor -> // Use 'use' to ensure the cursor is closed automatically
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)


        while (cursor.moveToNext()) {
            val trackId = cursor.getLong(idColumn)
            val trackName = cursor.getString(titleColumn)
            val trackArtist = cursor.getString(artistColumn) ?: "Unknown Artist" // Handle null artist
            val trackAlbum = cursor.getString(albumColumn) ?: "Unknown Album"   // Handle null album
            val trackPath = cursor.getString(pathColumn)
            val trackDuration = cursor.getInt(durationColumn) // Get duration

            val musicUri: Uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                trackId
            )

            val song = Song(
                songName = trackName,
                artistName = trackArtist,
                albumName = trackAlbum,
                uri = musicUri,
                path = trackPath
            )
            // The Song data class was:
            // data class Song(
            //    val songName: String,
            //    val artistName: String,
            //    val albumName: String,
            //    var uri: Uri,
            //    var path: String
            // ) {
            //    val uuid: UUID = UUID.randomUUID()
            //    var trackDuration: Int = 0
            //    var trackCover: Uri? = null
            // }
            // So, we need to set trackDuration if it's part of the model and loaded.
            song.trackDuration = trackDuration // Set duration if your model has it

            tempAudioList.add(song)
        }
    } // Cursor is closed automatically here

    return tempAudioList
}
