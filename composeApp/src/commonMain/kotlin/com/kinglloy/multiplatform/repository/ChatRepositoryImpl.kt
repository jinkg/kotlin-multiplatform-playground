package com.kinglloy.multiplatform.repository

import com.kinglloy.multiplatform.model.ChatDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepositoryImpl : ChatRepository {
    override fun getChats(): Flow<List<ChatDetail>> {
        return flow {
            emit(listOf(ChatDetail("1"), ChatDetail("2"), ChatDetail("3")))
        }
    }
}