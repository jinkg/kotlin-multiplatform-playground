package com.kinglloy.multiplatform.data

import androidx.room.Dao
import androidx.room.Query
import com.kinglloy.multiplatform.model.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("INSERT INTO Chat (id) VALUES (NULL)")
    suspend fun createChat(): Long

    @Query("SELECT * FROM Chat")
    fun loadAll(): Flow<List<Chat>>
}
