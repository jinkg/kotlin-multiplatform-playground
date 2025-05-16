package com.kinglloy.multiplatform.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.kinglloy.multiplatform.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single<AppDatabase> {
        getDatabaseBuilder().build()
    }
}

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "app_db_test_2")
    return Room.databaseBuilder<AppDatabase>(dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}