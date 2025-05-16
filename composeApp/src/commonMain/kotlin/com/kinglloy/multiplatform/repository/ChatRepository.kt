package com.kinglloy.multiplatform.repository

import com.kinglloy.multiplatform.model.ChatDetail
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<ChatDetail>>
}