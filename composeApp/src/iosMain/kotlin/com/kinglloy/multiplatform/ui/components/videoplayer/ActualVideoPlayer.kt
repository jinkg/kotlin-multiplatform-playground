package com.kinglloy.multiplatform.ui.components.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.play
import platform.AVFoundation.pause
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.setVolume
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.Foundation.NSURL
import platform.UIKit.UIView
// It's good practice to also import platform.UIKit.backgroundColor for clarity if used.
// import platform.UIKit.UIColor // If you were to set background color for example

actual class VideoPlayerController {
    internal var avPlayer: AVPlayer? = null
        private set
    internal var avPlayerViewController: AVPlayerViewController? = null // Made internal for composable access
        private set

    private var currentUrl: String? = null
    private var playWhenReady: Boolean = true

    // Initialize is called from the Composable with the native view controller
    fun initializeWithViewController(playerViewController: AVPlayerViewController) {
        if (this.avPlayerViewController != null && this.avPlayerViewController == playerViewController) {
            // Already initialized with the same controller
            return
        }
        this.avPlayerViewController = playerViewController
        this.avPlayer = playerViewController.player ?: AVPlayer() // Create player if controller doesn't have one
        this.avPlayerViewController!!.player = this.avPlayer // Ensure player is set on controller

        // Apply stored state
        if (playWhenReady) {
            this.avPlayer?.play()
        } else {
            this.avPlayer?.pause()
        }
        currentUrl?.let { setupMediaItem(it) }
    }

    private fun setupMediaItem(url: String) {
        val nsUrl = NSURL(string = url)
        if (nsUrl != null) {
            val playerItem = AVPlayerItem(uRL = nsUrl)
            avPlayer?.replaceCurrentItemWithPlayerItem(playerItem)
        } else {
            println("Error: Invalid URL string for AVPlayer: $url")
            // Consider some error state handling
        }
    }
    
    // Called by the Composable to load/change the URL
    internal fun loadUrl(url: String) {
        currentUrl = url // Store in case not initialized yet
        if (avPlayer != null && avPlayerViewController != null) {
            setupMediaItem(url)
        }
    }

    actual fun play() {
        playWhenReady = true
        avPlayer?.play()
    }

    actual fun pause() {
        playWhenReady = false
        avPlayer?.pause()
    }

    actual fun stop() {
        playWhenReady = false
        avPlayer?.pause()
        avPlayer?.replaceCurrentItemWithPlayerItem(null)
    }

    actual fun setVolume(volume: Float) {
        avPlayer?.volume = volume.coerceIn(0.0f, 1.0f)
    }

    fun release() {
        // Pause and clear item
        avPlayer?.pause()
        avPlayer?.replaceCurrentItemWithPlayerItem(null)
        
        // Nullify references. The AVPlayerViewController's lifecycle
        // is managed by UIKit when integrated via UIKitView.
        // We don't release the avPlayerViewController itself here as it's provided by remember {}.
        avPlayer = null 
        // avPlayerViewController = null // Don't nullify the one managed by remember {}
        currentUrl = null
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    url: String,
    controller: VideoPlayerController
) {
    // Create and remember AVPlayerViewController instance
    val playerViewController = remember { AVPlayerViewController() }

    // Initialize the controller with AVPlayerViewController
    LaunchedEffect(playerViewController, controller) {
        controller.initializeWithViewController(playerViewController)
    }

    // React to URL changes
    LaunchedEffect(url, controller) {
        controller.loadUrl(url)
    }
    
    // Lifecycle management for the controller
    DisposableEffect(Unit) { // Or key on controller if it can change
        onDispose {
            controller.release()
        }
    }

    UIKitView(
        factory = {
            // This is the view from AVPlayerViewController
            playerViewController.view.also {
                // You can configure playerViewController here if needed, e.g.:
                // playerViewController.showsPlaybackControls = true // Default is true
                // playerViewController.entersFullScreenWhenPlaybackBegins = false
            }
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            view.setFrame(rect)
        },
        update = { _ ->
            // Update logic if needed, e.g., for properties not directly bindable
            // or when controller state needs to reflect on the view externally.
            // For basic video playback, AVPlayerViewController handles its own UI.
        },
        modifier = modifier
    )
}
