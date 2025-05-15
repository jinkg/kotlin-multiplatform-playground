package com.kinglloy.multiplatform.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kinglloy.multiplatform.ActivityCloser
import com.kinglloy.multiplatform.ui.camera.Camera
import com.kinglloy.multiplatform.ui.home.chatlist.ChatListNavHost
import com.kinglloy.multiplatform.ui.home.settings.SettingsNavHost
import com.kinglloy.multiplatform.ui.home.timeline.TimelineNavHost
import com.kinglloy.multiplatform.ui.navigation.Route
import com.kinglloy.multiplatform.ui.navigation.SocialiteNavSuite
import com.kinglloy.multiplatform.ui.theme.SocialTheme

val mainTabRoute = Route.ChatsListTab

val initialRouteForTab = mapOf<Route, Route>(
    Route.TimelineTab to Route.Timeline,
    Route.ChatsListTab to Route.ChatsList,
    Route.SettingsTab to Route.Settings
)

@Composable
fun Main(activityCloser: ActivityCloser?) {
    val modifier = Modifier.fillMaxSize()
    SocialTheme {
        MainNavigation(modifier, activityCloser)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainTabsScreen(
    rootNavController: NavHostController,
    modifier: Modifier,
    activityCloser: ActivityCloser?
) {
    val timelineNavController = rememberNavController()
    val chatListNavController = rememberNavController()
    val settingsNavController = rememberNavController()

    val navControllers = mapOf(
        Route.TimelineTab to timelineNavController,
        Route.ChatsListTab to chatListNavController,
        Route.SettingsTab to settingsNavController
    )

    var currentRoute by rememberSaveable { mutableStateOf<Route>(mainTabRoute) }
    val currentTabNavController = navControllers[currentRoute] ?: chatListNavController

    BackHandler(enabled = true) {
        if (currentTabNavController.previousBackStackEntry != null) {
            currentTabNavController.popBackStack()
        } else if (currentRoute != mainTabRoute) {
            currentRoute = mainTabRoute
        } else {
            if (!rootNavController.popBackStack()) {
                activityCloser?.requestClose()
            }
        }
    }

    SocialiteNavSuite(
        currentRoute = currentRoute,
        onTabSelected = {
            if (currentRoute == it) {
                currentTabNavController.navigate(initialRouteForTab[currentRoute]!!) {
                    popUpTo(currentTabNavController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                currentRoute = it
            }
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when (currentRoute) {
                Route.ChatsListTab -> ChatListNavHost(rootNavController, chatListNavController)
                Route.TimelineTab -> TimelineNavHost(rootNavController, timelineNavController)
                Route.SettingsTab -> SettingsNavHost(rootNavController, settingsNavController)
                else -> {
                    throw IllegalStateException("The tab($currentRoute) doesn't exist")
                }
            }
        }
    }
}

@Composable
fun MainNavigation(
    modifier: Modifier,
    activityCloser: ActivityCloser?
) {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = Route.Home
    ) {
        composable<Route.Home> {
            MainTabsScreen(
                rootNavController = rootNavController,
                modifier = modifier,
                activityCloser
            )
        }
        composable<Route.Camera> { backStackEntry ->
            val route: Route.Camera = backStackEntry.toRoute()
            val chatId = route.chatId
            Camera(chatId = chatId)
        }
    }
}