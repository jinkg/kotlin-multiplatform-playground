package com.kinglloy.multiplatform.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kinglloy.multiplatform.data.AppDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        getDatabaseBuilder(get()).build()
    }
}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("appdb_test_1")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}