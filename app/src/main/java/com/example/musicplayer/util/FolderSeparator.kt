package com.example.musicplayer.util

import com.example.musicplayer.model.Folder
import com.example.musicplayer.model.Song // Ensure this is the Kotlin data class

//fun main() { // Test main function, can be removed or kept for testing
//    val s = "/storage/emulated/0/Music/Aaron-U-Turn-(Lili)-128.mp3"
//    val last = s.lastIndexOf("/")
//    val s1 = s.substring(0, last)
//    val last1 = s1.lastIndexOf("/")
//    println(s1.substring(last1 + 1))
//}

fun getFolders(songs: List<Song>): List<Folder> {
    val folders = mutableListOf<Folder>()

    for (song in songs) {
        val folderName = getFolderNameFromPath(song.path)
        var folderExists = false
        for (folder in folders) {
            if (folder.folderName == folderName) {
                folderExists = true
                break
            }
        }

        if (!folderExists) {
            val songsInThisFolder = mutableListOf<String>()
            for (s in songs) {
                if (getFolderNameFromPath(s.path) == folderName) {
                    songsInThisFolder.add(s.path)
                }
            }
            folders.add(Folder(folderName, songsInThisFolder))
        }
    }
    return folders
}

private fun getFolderNameFromPath(path: String): String {
    val lastSeparator = path.lastIndexOf('/')
    if (lastSeparator == -1) return "" // Or handle as an error/default

    val parentPath = path.substring(0, lastSeparator)
    val secondLastSeparator = parentPath.lastIndexOf('/')
    if (secondLastSeparator == -1) return parentPath // If the path is like "/FolderName"

    return parentPath.substring(secondLastSeparator + 1)
}
