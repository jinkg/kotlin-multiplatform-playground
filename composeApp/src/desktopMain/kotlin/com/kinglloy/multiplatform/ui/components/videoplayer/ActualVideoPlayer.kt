package com.kinglloy.multiplatform.ui.components.videoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button // Or androidx.compose.material.Button if M2
import androidx.compose.material3.Text   // Or androidx.compose.material.Text if M2
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import java.awt.Component // AWT Component
import javax.swing.JPanel // Swing JPanel for factory, if needed directly, but Component is enough for SwingPanel

actual class VideoPlayerController {
    // Holds the EmbeddedMediaPlayerComponent, which is a Swing Component
    internal var mediaPlayerComponent: EmbeddedMediaPlayerComponent? = null
        private set
    
    // Direct access to the media player instance
    private var mediaPlayer: EmbeddedMediaPlayer? = null

    private var factory: MediaPlayerFactory? = null
    private var currentUrl: String? = null

    var isVlcFound by mutableStateOf(true) // Observable state for UI
        private set
    var errorMessage by mutableStateOf<String?>(null) // For more detailed errors
        private set

    fun initialize() {
        if (mediaPlayerComponent != null) return

        try {
            // This can throw VlcNotFoundException or other runtime errors if VLC is not found/configured
            factory = MediaPlayerFactory() 
            mediaPlayerComponent = EmbeddedMediaPlayerComponent(factory, null, null, null, null)
            mediaPlayer = mediaPlayerComponent?.mediaPlayer() // Get the actual player instance

            if (mediaPlayer == null) {
                throw Exception("Failed to get EmbeddedMediaPlayer instance from component.")
            }
            isVlcFound = true
            errorMessage = null
            currentUrl?.let { if (isVlcFound) loadMedia(it) }

        } catch (e: UnsatisfiedLinkError) {
            errorMessage = "VLC native libraries not found. Please ensure VLC is installed and in the system path. Error: ${e.message}"
            println(errorMessage)
            isVlcFound = false
        } catch (e: Exception) { // Catch VlcNotFoundException and other generic exceptions
            errorMessage = "Error initializing VLCJ: ${e.message}. Please ensure VLC is installed."
            println(errorMessage)
            isVlcFound = false
        }
    }

    private fun loadMedia(url: String) {
        // mediaPlayer?.media()?.play(url) // For vlcj 4.x
        // For vlcj 4.x, play is on ControlsApi
        mediaPlayer?.controls()?.play(url)
    }

    // To be called by the Composable to load/change the URL
    internal fun loadUrl(url: String) {
        currentUrl = url
        if (mediaPlayer != null && isVlcFound) {
            loadMedia(url)
        }
    }

    actual fun play() {
        if (!isVlcFound || mediaPlayer == null) return
        mediaPlayer?.controls()?.play()
    }

    actual fun pause() {
        if (!isVlcFound || mediaPlayer == null) return
        mediaPlayer?.controls()?.pause()
    }

    actual fun stop() {
        if (!isVlcFound || mediaPlayer == null) return
        mediaPlayer?.controls()?.stop()
    }

    actual fun setVolume(volume: Float) {
        if (!isVlcFound || mediaPlayer == null) return
        val vlcVolume = (volume.coerceIn(0f, 1f) * 100).toInt() // Map 0.0-1.0 to 0-100 for VLC
        mediaPlayer?.audio()?.setVolume(vlcVolume)
    }

    fun release() {
        // mediaPlayer?.release() // This is for MediaPlayer, not EmbeddedMediaPlayer
        mediaPlayerComponent?.release() // This releases the EmbeddedMediaPlayer and the factory through it if it was the last one.
        factory?.release() // Explicitly release factory if shared or to be sure
        mediaPlayerComponent = null
        mediaPlayer = null
        factory = null
    }
}

@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    url: String,
    controller: VideoPlayerController
) {
    LaunchedEffect(controller) {
        controller.initialize()
    }

    LaunchedEffect(url, controller.isVlcFound) {
        if (controller.isVlcFound) {
            controller.loadUrl(url)
        }
    }

    DisposableEffect(controller) { // Key on controller if it can change
        onDispose {
            controller.release()
        }
    }

    if (!controller.isVlcFound) {
        Box(modifier = modifier.background(Color.Black), contentAlignment = Alignment.Center) {
            Text(
                text = controller.errorMessage ?: "VLC Media Player not found or failed to initialize.",
                color = Color.White,
                modifier = Modifier.padding(16.dp) // Add some padding
            )
        }
        return
    }

    Column(modifier = modifier) { // Outer Column for video and controls
        if (!controller.isVlcFound) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Make error box take available space
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = controller.errorMessage ?: "VLC Media Player not found or failed to initialize.",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            controller.mediaPlayerComponent?.let { component ->
                SwingPanel(
                    factory = { component },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Video panel takes most of the space
                )

                // Controls Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { controller.play() }) {
                        Text("Play")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { controller.pause() }) {
                        Text("Pause")
                    }
                    // TODO: Optionally add a simple volume slider or +/- buttons here
                    // For setVolume, remember it takes a Float 0.0 to 1.0
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Video player component not available.", color = Color.White)
                }
            }
        }
    }
}
