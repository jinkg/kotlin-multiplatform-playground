package com.kinglloy.multiplatform.ui.home.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Timeline(
    onProfileClicked: (userId: String) -> Unit,
    onDetailClicked: (postId: String) -> Unit,
    onCameraClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Timeline")
            Button(onClick = { onProfileClicked("Timeline - user#1") }) {
                Text(text = "Open Profile")
            }

            Button(onClick = { onDetailClicked("Timeline - post#1") }) {
                Text(text = "Open Detail")
            }

            Button(onClick = onCameraClicked) {
                Text(text = "Open Camera")
            }
        }
    }
}