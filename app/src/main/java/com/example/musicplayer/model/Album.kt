package com.example.musicplayer.model

import java.io.Serializable

data class Album(
    var songsOfAlbum: MutableList<String>? = null,
    var albumName: String? = null
) : Serializable
