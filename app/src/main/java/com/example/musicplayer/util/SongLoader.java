package com.example.musicplayer.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongLoader {


    public static List<Song> loadAllSong(Context context){
        List<Song> tempAudioList = new ArrayList<>();

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor =context.getContentResolver().query(
                songUri,
                null,
                null,
                null,
                null);

        if (songCursor != null && songCursor.moveToFirst()) {

            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);


            do {

                String trackName = songCursor.getString(songTitle);
                String trackArtist = songCursor.getString(songArtist);
                String trackAlbum = songCursor.getString(songAlbum);
                String trackId = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                Uri musicUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(trackId));
               tempAudioList.add(new Song(
                       trackName,
                       trackArtist,
                       trackAlbum,
                       musicUri)
               );
            } while (songCursor.moveToNext());
            songCursor.close();
        }
        return tempAudioList;
    }
}
