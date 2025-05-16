package com.kinglloy.multiplatform.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.kinglloy.multiplatform.data.AppDatabase
import com.kinglloy.multiplatform.data.DB_FILE_NAME
import com.kinglloy.multiplatform.data.getRoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformModule = module {
    single<AppDatabase> {
        getRoomDatabase(getDatabaseBuilder())
    }
}

@OptIn(ExperimentalForeignApi::class)
fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val docDir = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    val dbFilePath = requireNotNull(docDir?.path) + "/$DB_FILE_NAME"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath
    )
}