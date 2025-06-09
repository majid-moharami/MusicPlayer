package com.example.musicplayer.model

import java.io.Serializable

data class Folder(
    var folderName: String,
    var songsFolderPath: MutableList<String>
) : Serializable
