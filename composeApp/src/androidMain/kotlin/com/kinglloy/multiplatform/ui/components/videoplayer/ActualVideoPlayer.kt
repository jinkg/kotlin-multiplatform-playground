package com.kinglloy.multiplatform.ui.components.videoplayer

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.VideoPlayer as Media3VideoPlayer // Alias for clarity

actual class VideoPlayerController {
    internal var exoPlayer: ExoPlayer? = null
        private set

    private var isInitialized = false
    private var currentUrl: String? = null
    private var applicationContext: Context? = null
    
    // Keep track of playWhenReady state to restore after foregrounding or explicit play call
    private var playWhenReady = true 

    fun initialize(context: Context) {
        if (isInitialized) return
        // Use application context to avoid leaking Activity context
        this.applicationContext = context.applicationContext
        exoPlayer = ExoPlayer.Builder(this.applicationContext!!).build().also {
            it.playWhenReady = this.playWhenReady // Apply initial playWhenReady state
        }
        isInitialized = true
        // If a URL was set before initialization, load it now.
        currentUrl?.let { setMediaItemInternal(it) }
    }

    actual fun play() {
        playWhenReady = true
        exoPlayer?.playWhenReady = true
        exoPlayer?.play() // Ensure playback starts if paused
    }

    actual fun pause() {
        playWhenReady = false
        exoPlayer?.pause()
    }

    actual fun stop() {
        playWhenReady = false // Typically stop implies not playing when ready next time
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems() // Clears playlist and resets player state
    }

    actual fun setVolume(volume: Float) {
        exoPlayer?.volume = volume.coerceIn(0f, 1f)
    }

    // This should be called when the controller is no longer needed.
    fun release() {
        if (!isInitialized) return
        exoPlayer?.release()
        exoPlayer = null
        isInitialized = false
        applicationContext = null
        currentUrl = null
    }

    // Internal function to set media item, controller manages its player
    // This can be called by the Composable when the URL changes.
    internal fun loadUrl(url: String) {
        currentUrl = url
        if (isInitialized && exoPlayer != null) {
            setMediaItemInternal(url)
        }
    }

    private fun setMediaItemInternal(url: String) {
        exoPlayer?.let {
            val mediaItem = MediaItem.fromUri(url)
            it.setMediaItem(mediaItem)
            // Only prepare if not already prepared or if media item changed significantly
            // ExoPlayer handles re-preparation internally if needed when setMediaItem is called.
            it.prepare()
        }
    }
    
    internal fun onLifecycleEvent(event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                // Reinitialize or resume player if needed and was playing
                if (isInitialized && exoPlayer != null && playWhenReady) {
                    exoPlayer?.playWhenReady = true 
                }
            }
            Lifecycle.Event.ON_STOP -> {
                // Pause player but retain playWhenReady state
                if (isInitialized && exoPlayer != null) {
                    // playWhenReady is preserved
                    exoPlayer?.playWhenReady = false 
                }
            }
            Lifecycle.Event.ON_DESTROY -> {
                // This is a final cleanup. If the controller is meant to survive
                // configuration changes, this needs more careful handling,
                // possibly by a ViewModel.
                // release() 
            }
            else -> {}
        }
    }
}

@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    url: String,
    controller: VideoPlayerController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State to manage the ExoPlayer instance within the Composable lifecycle
    // This ensures that if the controller is remembered across recompositions,
    // the player is correctly initialized and released.
    var playerInstance by remember { mutableStateOf<ExoPlayer?>(null) }

    LaunchedEffect(controller, context) {
        controller.initialize(context.applicationContext)
        playerInstance = controller.exoPlayer
    }

    // Load URL when it changes or when the player becomes available
    LaunchedEffect(url, playerInstance) {
        playerInstance?.let { // Ensure player is initialized
            controller.loadUrl(url)
        }
    }
    
    DisposableEffect(lifecycleOwner, controller) {
        val observer = LifecycleEventObserver { _, event ->
            controller.onLifecycleEvent(event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Decide if controller itself should be released or just the player it holds.
            // If controller is managed by a ViewModel, release might happen there.
            // For now, if the Composable is disposed, we release the controller's player.
            // This assumes the controller is scoped to this Composable instance.
            controller.release() 
            playerInstance = null
        }
    }

    playerInstance?.let { alivePlayer ->
        Media3VideoPlayer(
            player = alivePlayer,
            modifier = modifier
        )
    } ?: run {
        Box(modifier = modifier.background(Color.Black)) // Placeholder
    }
}
