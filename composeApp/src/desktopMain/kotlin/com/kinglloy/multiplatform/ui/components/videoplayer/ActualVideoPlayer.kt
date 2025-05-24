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
    private var playPending = false

    var isReady by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>("Player not initialized.")

    fun initialize() {
        if (isReady) return // Already initialized and ready
        if (mediaPlayerComponent != null && !isReady) {
            // This case might indicate a previous failed initialization or release without full cleanup.
            // Proceeding could lead to resource leaks or unexpected behavior.
            // For safety, one might want to log this or even throw an error.
            // For now, we'll let it proceed to re-initialize.
            println("Warning: Re-initializing controller that has a media player component but is not marked ready.")
        }

        statusMessage = "Initializing..."
        try {
            // This can throw VlcNotFoundException or other runtime errors if VLC is not found/configured
            factory = MediaPlayerFactory()
            mediaPlayerComponent = EmbeddedMediaPlayerComponent(factory, null, null, null, null)
            mediaPlayer = mediaPlayerComponent?.mediaPlayer() // Get the actual player instance

            if (mediaPlayer == null) {
                throw Exception("Failed to get EmbeddedMediaPlayer instance from component.")
            }
            isReady = true
            statusMessage = null // Or "Player Ready"

        } catch (e: UnsatisfiedLinkError) {
            statusMessage = "VLC native libraries not found. Please ensure VLC is installed and in system path. Details: ${e.message}"
            println(statusMessage)
            isReady = false
        } catch (e: Exception) { // Catch VlcNotFoundException and other generic exceptions
            statusMessage = "Error initializing VLCJ: ${e.message}. Please ensure VLC is installed."
            println(statusMessage)
            isReady = false
        }
    }

    // internal fun loadMedia(url: String) { // This was the old function that played
    //    mediaPlayer?.controls()?.play(url)
    // }

    // To be called by the Composable to load/change the URL
    internal fun loadUrl(url: String) { // Renamed from previous internal loadUrl
        currentUrl = url
        // If mediaPlayer is ready, one might prepare the media here,
        // but vlcj's play(mrl) usually handles it.
        // If play is pending, and surface is ready, this new URL should be picked up.
        if (playPending && mediaPlayerComponent?.isDisplayable == true) {
            currentUrl?.let { mediaPlayer?.media()?.play(it) } // Play new URL if pending
            // playPending remains false if it was already false.
            // If playPending was true, it means play() was called before surface was ready.
            // We can set it to false here as play is now initiated.
            // playPending = false // Let surfaceBecameDisplayable or play() manage this.
        }
    }

    actual fun play() {
        if (!isReady || mediaPlayer == null) return // Rely on isReady state
        if (currentUrl == null) {
            statusMessage = "No URL loaded to play."
            println(statusMessage)
            return
        }

        if (mediaPlayerComponent?.isDisplayable == true) {
            mediaPlayer?.media()?.play(currentUrl!!)
            playPending = false
        } else {
            playPending = true
        }
    }

    actual fun pause() {
        if (!isReady || mediaPlayer == null) return
        playPending = false // If user explicitly pauses, don't autoplay on surface ready
        mediaPlayer?.controls()?.pause()
    }

    actual fun stop() {
        if (!isReady || mediaPlayer == null) return
        playPending = false // If user explicitly stops, don't autoplay on surface ready
        mediaPlayer?.controls()?.stop()
    }

    actual fun setVolume(volume: Float) {
        if (!isReady || mediaPlayer == null) return
        val vlcVolume = (volume.coerceIn(0f, 1f) * 100).toInt() // Map 0.0-1.0 to 0-100 for VLC
        mediaPlayer?.audio()?.setVolume(vlcVolume)
    }

    fun release() {
        mediaPlayerComponent?.release()
        factory?.release()
        mediaPlayerComponent = null
        mediaPlayer = null
        factory = null
        isReady = false
        statusMessage = "Player released."
    }

    internal fun surfaceBecameDisplayable() {
        if (playPending && mediaPlayerComponent?.isDisplayable == true) {
            currentUrl?.let { urlToPlay ->
                mediaPlayer?.media()?.play(urlToPlay)
            }
            playPending = false
        }
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

    LaunchedEffect(url, controller.isReady) {
        if (controller.isReady) {
            controller.loadUrl(url)
            controller.play() // Request play, it will be handled by playPending if surface not ready
        }
    }

    DisposableEffect(controller) { // Key on controller if it can change
        onDispose {
            controller.release()
        }
    }

    if (!controller.isReady) {
        Box(modifier = modifier.background(Color.Black), contentAlignment = Alignment.Center) {
            Text(
                text = controller.statusMessage ?: "VLC Media Player not found or failed to initialize.",
                color = Color.White,
                modifier = Modifier.padding(16.dp) // Add some padding
            )
        }
        return
    }

    Column(modifier = modifier) { // Outer Column for video and controls
        if (!controller.isReady) {
            Box(
                modifier = Modifier // The main modifier for the VideoPlayer itself
                    .fillMaxWidth() // Ensure this Box fills the space allocated to VideoPlayer
                    .weight(1f) // This was in a Column, so weight is appropriate
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = controller.statusMessage ?: "Video player is not ready or has encountered an error.",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // This 'else' means controller.isReady is true.
            // mediaPlayerComponent should be non-null if isReady is true due to initialize logic.
            controller.mediaPlayerComponent?.let { component ->
                // SwingPanel factory and update lambda for surfaceBecameDisplayable
                SwingPanel(
                    factory = { component }, // Use the captured component from let
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Video panel takes most of the space
                    update = { comp -> // Can rename to avoid conflict, or use _ if not used
                        controller.surfaceBecameDisplayable()
                    }
                )

                // Controls Row (Play/Pause buttons)
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
                }
            } ?: run {
                // This state (isReady == true, but mediaPlayerComponent == null) should ideally not be reached
                // if the initialize and release logic is correct.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Red), // Distinct background for unexpected error
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: Player is marked ready but component is unavailable.", color = Color.White)
                }
            }
        }
    }
}
