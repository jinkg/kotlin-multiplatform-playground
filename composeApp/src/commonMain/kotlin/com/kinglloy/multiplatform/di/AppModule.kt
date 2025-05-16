package com.kinglloy.multiplatform.di

import com.kinglloy.multiplatform.repository.ChatRepository
import com.kinglloy.multiplatform.repository.ChatRepositoryImpl
import com.kinglloy.multiplatform.ui.home.chatlist.ChatListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ChatRepository> { ChatRepositoryImpl() }

    viewModel {
        ChatListViewModel(get())
    }
}