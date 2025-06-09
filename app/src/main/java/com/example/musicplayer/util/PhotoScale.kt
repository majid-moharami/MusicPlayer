package com.example.musicplayer.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.media.MediaMetadataRetriever
import com.example.musicplayer.model.Song // Ensure this is the Kotlin data class

// This function seems incomplete as it doesn't return or use the loaded picture.
// It might be intended for a different purpose or is unused.
fun bitMapFactorySongCover(song: Song): ByteArray? {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    return try {
        mediaMetadataRetriever.setDataSource(song.path)
        mediaMetadataRetriever.embeddedPicture
    } catch (e: Exception) {
        // Handle exceptions, e.g., file not found, invalid data source
        null
    } finally {
        try {
            mediaMetadataRetriever.release()
        } catch (e: Exception) {
            // Ignore errors on release
        }
    }
}

fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap? {
    // Get the dimensions of the bitmap
    val bmOptions = BitmapFactory.Options()
    bmOptions.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, bmOptions)

    val srcWidth = bmOptions.outWidth
    val srcHeight = bmOptions.outHeight

    if (srcWidth <= 0 || srcHeight <= 0) { // Check for invalid image dimensions
        return null
    }

    // Determine how much to scale down the image
    val scaleFactor = Math.max(1, Math.min(srcWidth / destWidth, srcHeight / destHeight))

    // Decode the image file into a Bitmap sized to fill the View
    bmOptions.inJustDecodeBounds = false
    bmOptions.inSampleSize = scaleFactor
    // bmOptions.inPurgeable = true // inPurgeable is deprecated in API Level 21

    return BitmapFactory.decodeFile(path, bmOptions)
}

fun getScaledBitmap(path: String, activity: Activity): Bitmap? {
    val display = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        activity.display
    } else {
        @Suppress("DEPRECATION")
        activity.windowManager.defaultDisplay
    }
    val size = Point()
    if (display != null) {
        @Suppress("DEPRECATION")
        display.getSize(size)
    } else {
        // Fallback or error handling if display is null
        return null
    }
    return getScaledBitmap(path, size.x, size.y)
}
