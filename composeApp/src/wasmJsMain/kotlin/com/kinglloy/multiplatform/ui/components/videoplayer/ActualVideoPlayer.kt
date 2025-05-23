package com.kinglloy.multiplatform.ui.components.videoplayer

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.web.dom.Video as ComposeVideo // Alias to avoid confusion
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.autoplay
import org.jetbrains.compose.web.attributes.controls
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.w3c.dom.HTMLVideoElement

actual class VideoPlayerController {
    internal var htmlVideoElement: HTMLVideoElement? = null
        private set

    private var currentUrl: String? = null
    private var playWhenReadyInternal = true // Internal state for autoplay and play calls

    fun initialize(element: HTMLVideoElement, initialUrl: String) {
        if (htmlVideoElement == element && currentUrl == initialUrl) return // Avoid re-initialization with same element and url
        
        htmlVideoElement = element
        currentUrl = initialUrl // Set currentUrl upon initialization
        
        htmlVideoElement?.src = initialUrl
        htmlVideoElement?.autoplay = playWhenReadyInternal
        htmlVideoElement?.controls = true // Ensure controls are enabled by default

        if (playWhenReadyInternal) {
            htmlVideoElement?.play()?.catch { 
                println("Initial play attempt failed (browser policy likely): ${it.message}")
            }
        }
    }

    actual fun play() {
        playWhenReadyInternal = true
        htmlVideoElement?.play()?.catch { 
            println("Error trying to play video: ${it.message}")
        }
    }

    actual fun pause() {
        playWhenReadyInternal = false
        htmlVideoElement?.pause()
    }

    actual fun stop() {
        playWhenReadyInternal = false
        htmlVideoElement?.pause()
        htmlVideoElement?.removeAttribute("src")
        htmlVideoElement?.load() // Required to reflect src removal
        currentUrl = null 
    }

    actual fun setVolume(volume: Float) {
        htmlVideoElement?.volume = volume.coerceIn(0.0f, 1.0f).toDouble()
    }

    internal fun loadUrl(url: String) {
        currentUrl = url // Update current URL
        htmlVideoElement?.let {
            it.src = url
            it.load() // Call load() to apply the new source
            if (playWhenReadyInternal) {
                it.play()?.catch { ex -> 
                    println("Error playing after loadUrl: ${ex.message}")
                }
            }
        }
    }

    fun release() {
        htmlVideoElement?.pause()
        // No explicit 'release' for HTML elements, browser GC handles it.
        // Nullify reference for Kotlin GC and to prevent further ops.
        htmlVideoElement = null
        currentUrl = null
    }
}

@Composable
actual fun VideoPlayer(
    modifier: Modifier, // This modifier is from Compose, may not directly map to HTML attrs
    url: String,
    controller: VideoPlayerController
) {
    var videoElement: HTMLVideoElement? by remember { mutableStateOf(null) }

    // Initialize controller when the video element is available or URL changes
    // This LaunchedEffect will re-run if `url` changes, re-initializing with the new url.
    LaunchedEffect(url, videoElement, controller) {
        videoElement?.let { el ->
            controller.initialize(el, url)
        }
    }
    
    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    // The passed `modifier` in KMM common code is tricky for wasmJs.
    // Compose for Web uses its own styling system (e.g., style { width(100.percent); height(100.percent) }).
    // We'll apply a basic style for now, assuming the modifier is for sizing.
    // A more robust solution would involve converting Compose Modifiers to CSS,
    // or the caller using web-specific modifiers.
    ComposeVideo(
        attrs = {
            this.id("multiplatformVideoPlayer") // Useful for testing/debugging
            controls(true) // Show default browser controls

            // Attempt to apply modifier-like properties, this is a simplified approach
            // style {
            //    width(100.percent) // Example: fill width
            //    height(100.percent) // Example: fill height
            // }
            // The above style {} needs to be applied carefully based on how `modifier` is intended.
            // For now, we let the browser and parent elements control size.
            // The `modifier` from common code is not directly used here as it's not CSS.

            ref { htmlElement ->
                videoElement = htmlElement as HTMLVideoElement
                // Apply initial URL via controller once element is available
                // controller.initialize(htmlElement, url) // Done in LaunchedEffect now
                onDispose { videoElement = null }
            }
        }
    ) {
        // Source tag is implicitly handled by setting `videoElement.src`
        // If you need multiple <source> tags for different formats:
        // Source(attrs = { src(url); type("video/mp4") })
    }
}
