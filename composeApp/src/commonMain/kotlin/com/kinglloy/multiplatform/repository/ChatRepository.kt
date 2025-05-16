package com.kinglloy.multiplatform.repository

import com.kinglloy.multiplatform.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>

    suspend fun createChat(): Long
}