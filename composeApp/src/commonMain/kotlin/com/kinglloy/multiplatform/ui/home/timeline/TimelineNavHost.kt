package com.kinglloy.multiplatform.ui.home.timeline

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.kinglloy.multiplatform.ui.navigation.ChildNavigation
import com.kinglloy.multiplatform.ui.navigation.Route

@Composable
fun TimelineNavHost(
    rootNavController: NavHostController,
    navController: NavHostController
) {
    ChildNavigation(
        rootNavController,
        navController,
        startDestination = Route.Timeline
    ) {
        Timeline(
            onProfileClicked = {
                navController.navigate(Route.Profile(it))
            },
            onDetailClicked = {
                navController.navigate(Route.PostDetail(it))
            },
            onCameraClicked = {
                rootNavController.navigate(Route.Camera(1))
            },
            Modifier.fillMaxSize()
        )
    }
}