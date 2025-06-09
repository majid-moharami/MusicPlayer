package com.example.musicplayer.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Movie
import android.net.Uri
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.io.FileNotFoundException
import java.io.InputStream

class GifImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var inputStream: InputStream? = null
    private var movie: Movie? = null
    private var movieWidth: Int = 0
    private var movieHeight: Int = 0
    private var movieStart: Long = 0L

    init {
        // The original Java code had this logic:
        // if (attrs.getAttributeName(1).equals("background")) {
        // int id = Integer.parseInt(attrs.getAttributeValue(1).substring(1));
        // setGifImageResource(id);
        // }
        // This is not a safe way to handle attributes. Standard way is to use TypedArray.
        // For now, keeping it simple. If custom attributes are needed, they should be declared in attrs.xml
        // and parsed using obtainStyledAttributes.
        // Example of how it might have been intended (if 'background' was a custom attribute):
        // attrs?.let {
        //     val typedArray = context.obtainStyledAttributes(it, R.styleable.GifImageView, 0, 0)
        //     val resId = typedArray.getResourceId(R.styleable.GifImageView_gifSource, -1)
        //     if (resId != -1) {
        //         setGifImageResource(resId)
        //     }
        //     typedArray.recycle()
        // }
        // Since 'background' is a standard Android attribute, this specific logic was likely incorrect
        // or for a custom 'gifSource' attribute that looked at 'background' by mistake.
        // For this conversion, I'm omitting this attribute parsing part as it's unclear and potentially buggy.
        // If a 'src' or 'gifSource' attribute is needed, it should be properly defined and parsed.

        isFocusable = true
    }

    private fun initializeMovie() {
        inputStream?.let {
            movie = Movie.decodeStream(it)
            movie?.let { mov ->
                movieWidth = mov.width()
                movieHeight = mov.height()
                if (movieWidth <= 0 || movieHeight <= 0) { // Handle case where movie dimensions are invalid
                    // Set to some default or report error
                    movieWidth = 1 // Avoid division by zero if used in onMeasure without proper dimensions
                    movieHeight = 1
                }
            } ?: run {
                // Movie decoding failed
                movieWidth = 1
                movieHeight = 1
            }
            requestLayout() // Request layout update after movie dimensions are known
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Respect padding
        val pLeft = paddingLeft
        val pRight = paddingRight
        val pTop = paddingTop
        val pBottom = paddingBottom

        val finalWidth = resolveSizeAndState(movieWidth + pLeft + pRight, widthMeasureSpec, 1)
        val finalHeight = resolveSizeAndState(movieHeight + pTop + pBottom, heightMeasureSpec, 1)

        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas) // Call super.onDraw for background, etc.

        val now = SystemClock.uptimeMillis()

        if (movieStart == 0L) {
            movieStart = now
        }

        movie?.let { mov ->
            val duration = if (mov.duration() == 0) 1000 else mov.duration()
            val relTime = ((now - movieStart) % duration).toInt()
            mov.setTime(relTime)

            // Consider padding when drawing
            val x = paddingLeft.toFloat()
            val y = paddingTop.toFloat()

            // Scaling logic if view size is different from movie size
            // This example draws the movie at its native size, at top-left (respecting padding)
            // For more complex scaling, you'd use canvas.save(), canvas.scale(), canvas.restore()
            mov.draw(canvas, x, y)
            invalidate() // Redraw for next frame
        }
    }

    fun setGifImageResource(resId: Int) {
        try {
            inputStream = context.resources.openRawResource(resId)
            initializeMovie()
        } catch (e: Exception) {
            Log.e("GifImageView", "Could not open resource: $resId", e)
        }
    }

    fun setGifImageUri(uri: Uri?) {
        uri?.let {
            try {
                inputStream = context.contentResolver.openInputStream(it)
                initializeMovie()
            } catch (e: FileNotFoundException) {
                Log.e("GifImageView", "File not found for URI: $uri", e)
            } catch (e: Exception) {
                Log.e("GifImageView", "Could not open URI: $uri", e)
            }
        }
    }
}
