package com.kinglloy.multiplatform.ui.home.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinglloy.multiplatform.repository.ChatRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatListViewModel(private val repository: ChatRepository) : ViewModel() {
    val chatList = repository.getChats().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addChat() {
        viewModelScope.launch {
            repository.createChat()
        }
    }
}