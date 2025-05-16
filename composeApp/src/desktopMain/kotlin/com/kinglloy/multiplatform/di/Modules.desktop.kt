package com.kinglloy.multiplatform.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.kinglloy.multiplatform.data.AppDatabase
import com.kinglloy.multiplatform.data.DB_FILE_NAME
import com.kinglloy.multiplatform.data.getRoomDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single<AppDatabase> {
        getRoomDatabase(getDatabaseBuilder())
    }
}

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), DB_FILE_NAME)
    return Room.databaseBuilder<AppDatabase>(dbFile.absolutePath)
        .setQueryCoroutineContext(Dispatchers.IO)
}