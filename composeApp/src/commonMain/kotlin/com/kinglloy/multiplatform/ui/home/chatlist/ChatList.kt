package com.kinglloy.multiplatform.ui.home.chatlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatList(
    onChatClicked: (chatId: Long) -> Unit,
    onProfileClicked: (userId: String) -> Unit,
    onDetailClicked: (postId: String) -> Unit,
    onCameraClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatListViewModel = koinViewModel(),
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val chatList by viewModel.chatList.collectAsStateWithLifecycle()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { viewModel.addChat() }) {
                Text(text = "Create Chat")
            }
            Text(text = "ChatList:")
            Text(text = chatList.joinToString())
            Text(text = "----------")
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