package com.kinglloy.multiplatform.repository

import com.kinglloy.multiplatform.data.ChatDao

class ChatRepositoryImpl(private val chatDao: ChatDao) : ChatRepository {
    override fun getChats() = chatDao.loadAll()
    override suspend fun createChat() = chatDao.createChat()
}