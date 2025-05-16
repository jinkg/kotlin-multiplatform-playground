package com.kinglloy.multiplatform.di

import com.kinglloy.multiplatform.data.AppDatabase
import com.kinglloy.multiplatform.data.ChatDao
import com.kinglloy.multiplatform.repository.ChatRepository
import com.kinglloy.multiplatform.repository.ChatRepositoryImpl
import com.kinglloy.multiplatform.ui.home.chatlist.ChatListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    single<ChatRepository> { ChatRepositoryImpl(get()) }

    single<ChatDao> { get<AppDatabase>().chatDao() }

    viewModel { ChatListViewModel(get()) }
}