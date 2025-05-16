package com.kinglloy.multiplatform.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kinglloy.multiplatform.data.AppDatabase
import com.kinglloy.multiplatform.data.DB_FILE_NAME
import com.kinglloy.multiplatform.data.getRoomDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        getRoomDatabase(getDatabaseBuilder(get()))
    }
}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}