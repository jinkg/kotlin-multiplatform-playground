package com.kinglloy.multiplatform.ui.home.chatlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ChatList(
    onChatClicked: (chatId: Long) -> Unit,
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
            Text(text = "ChatList")
            Button(onClick = { onProfileClicked("ChatList - user#1") }) {
                Text(text = "Open Profile")
            }

            Button(onClick = { onDetailClicked("ChatList - post#1") }) {
                Text(text = "Open Detail")
            }

            Button(onClick = onCameraClicked) {
                Text(text = "Open Camera")
            }
        }
    }
}