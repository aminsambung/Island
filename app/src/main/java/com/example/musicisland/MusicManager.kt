package com.example.musicisland

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

object MusicManager {
    var player: ExoPlayer? = null
    var currentTitle = "Tidak ada musik"
    var isPlaying = false

    fun initPlayer(context: Context) {
        if (player == null) player = ExoPlayer.Builder(context).build()
    }

    fun playRandomSong(context: Context): String {
        var title = "Tidak ada musik"
        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE),
                null, null, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getLong(0)
                title = cursor.getString(1)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                
                player?.setMediaItem(MediaItem.fromUri(uri))
                player?.prepare()
                player?.play()
                isPlaying = true
                currentTitle = title
            }
            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return title
    }
}
