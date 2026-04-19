package com.example.radiosemilladefenicaragua

import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    // Este es el reproductor que no se detiene al apagar la pantalla
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri("https://stream.zeno.fm/sk2ga7chsxktv")
            setMediaItem(mediaItem)
            prepare()
        }
        mediaSession = MediaSession.Builder(this, player).build()
    }

    // Le dice a Android qué sesión de música debe controlar
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}