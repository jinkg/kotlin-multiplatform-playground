package com.kinglloy.multiplatform.ui.home.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.kinglloy.multiplatform.ui.navigation.ChildNavigation
import com.kinglloy.multiplatform.ui.navigation.Route

@Composable
fun SettingsNavHost(
    rootNavController: NavHostController,
    navController: NavHostController
) {
    ChildNavigation(
        rootNavController,
        navController,
        startDestination = Route.Settings,
    ) {
        Settings(
            onProfileClicked = {},
            onDetailClicked = {},
            onCameraClicked = { rootNavController.navigate(Route.Camera(1)) },
            Modifier.fillMaxSize()
        )
    }
}