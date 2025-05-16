package com.kinglloy.multiplatform.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kinglloy.multiplatform.ui.home.postdetail.PostDetail
import com.kinglloy.multiplatform.ui.home.profile.Profile

@Composable
inline fun <reified T : Route> ChildNavigation(
    rootNavController: NavHostController,
    navController: NavHostController,
    startDestination: T,
    noinline startDestinationContent: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
        popExitTransition = {
            ExitTransition.None
        },
        popEnterTransition = {
            EnterTransition.None
        },
    ) {
        composable<T>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }) {
            startDestinationContent(it)
        }

        childPageComposable<Route.Profile>(startDestination) { backStackEntry ->
            val route: Route.Profile = backStackEntry.toRoute()
            val userId = route.userId
            Profile(
                userId = userId,
                onProfileClicked = { navController.navigate(Route.Profile(it)) },
                onDetailClicked = { navController.navigate(Route.PostDetail(it)) },
                onCameraClicked = { rootNavController.navigate(Route.Camera(1)) },
                modifier = Modifier.fillMaxSize(),
            )
        }
        childPageComposable<Route.PostDetail>(startDestination) { backStackEntry ->
            val route: Route.PostDetail = backStackEntry.toRoute()
            val postId = route.postId
            PostDetail(
                postId = postId,
                onProfileClicked = { navController.navigate(Route.Profile(it)) },
                onDetailClicked = { navController.navigate(Route.PostDetail(it)) },
                onCameraClicked = { rootNavController.navigate(Route.Camera(1)) },
                Modifier.fillMaxSize()
            )
        }
    }
}

inline fun <reified T : Route> NavGraphBuilder.childPageComposable(
    startDestination: Route,
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable<T>(
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        }) { backStackEntry ->
        content(backStackEntry)
    }
}