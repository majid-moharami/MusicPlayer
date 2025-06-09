package com.example.musicplayer.model

import java.io.Serializable

data class Artist(
    var songsOfArtist: MutableList<String>? = null,
    var artistName: String? = null
) : Serializable
