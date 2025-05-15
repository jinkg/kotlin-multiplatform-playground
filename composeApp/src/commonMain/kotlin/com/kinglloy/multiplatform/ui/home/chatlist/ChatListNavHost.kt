package com.kinglloy.multiplatform.ui.home.chatlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.kinglloy.multiplatform.ui.navigation.ChildNavigation
import com.kinglloy.multiplatform.ui.navigation.Route

@Composable
fun ChatListNavHost(
    rootNavController: NavHostController,
    navController: NavHostController
) {
    ChildNavigation(
        rootNavController,
        navController,
        startDestination = Route.ChatsList
    ) {
        ChatList(
            onChatClicked = { chatId ->
                rootNavController.navigate(Route.ChatThread(chatId))
            },
            onProfileClicked = {
                navController.navigate(Route.Profile(it))
            },
            onDetailClicked = {
                navController.navigate(Route.PostDetail(it))
            },
            onCameraClicked = {
                rootNavController.navigate(Route.Camera(1))
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}