package com.example.musicplayer.model

import android.net.Uri
import java.io.Serializable
import java.util.UUID

data class Song(
    val songName: String,
    val artistName: String,
    val albumName: String,
    var uri: Uri,
    var path: String
) : Serializable {
    val uuid: UUID = UUID.randomUUID()
    var trackDuration: Int = 0
    var trackCover: Uri? = null // Nullable as it's not initialized in constructor and can be set later
}
