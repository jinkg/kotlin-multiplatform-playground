package com.kinglloy.multiplatform.ui.components.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect class VideoPlayerController() {
    fun play()
    fun pause()
    fun stop()
    // Volume typically 0.0f to 1.0f. Consider platform differences.
    fun setVolume(volume: Float)
    // TODO: Add other necessary controls like:
    // fun seekTo(positionMs: Long)
    // fun getDurationMs(): Long
    // fun getCurrentPositionMs(): Long
    // fun isPlaying(): Boolean
    // fun loadUrl(url: String) // If the URL can change dynamically after creation
}

@Composable
expect fun VideoPlayer(
    modifier: Modifier = Modifier,
    url: String, // Initial URL to load
    controller: VideoPlayerController // Controller for playback management
)
