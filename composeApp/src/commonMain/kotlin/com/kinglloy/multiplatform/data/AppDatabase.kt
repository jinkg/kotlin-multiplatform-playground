package com.kinglloy.multiplatform.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kinglloy.multiplatform.model.Chat

@Database(
    entities = [
        Chat::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}