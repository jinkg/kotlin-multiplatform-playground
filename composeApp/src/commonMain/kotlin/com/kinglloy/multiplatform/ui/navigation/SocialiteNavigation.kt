package com.kinglloy.multiplatform.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.serialization.Serializable
import androidx.navigation.NavDestination.Companion.hasRoute
import com.kinglloy.multiplatform.parcelize.Parcelable
import com.kinglloy.multiplatform.parcelize.Parcelize
import kotlin_multiplatform_playground.composeapp.generated.resources.Res
import kotlin_multiplatform_playground.composeapp.generated.resources.chats
import kotlin_multiplatform_playground.composeapp.generated.resources.settings
import kotlin_multiplatform_playground.composeapp.generated.resources.timeline
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface Route : Parcelable {
    @Serializable
    @Parcelize
    data object TimelineTab : Route

    @Serializable
    @Parcelize
    data object Timeline : Route

    @Serializable
    @Parcelize
    data object ChatsListTab : Route

    @Serializable
    @Parcelize
    data object ChatsList : Route

    @Serializable
    @Parcelize
    data object SettingsTab : Route

    @Serializable
    @Parcelize
    data object Settings : Route

    @Serializable
    @Parcelize
    data object Home : Route

    @Serializable
    @Parcelize
    data class ChatThread(val chatId: Long, val text: String? = null) : Route

    @Serializable
    @Parcelize
    data class Camera(val chatId: Long) : Route

    @Serializable
    @Parcelize
    data class PhotoPicker(val chatId: Long) : Route

    @Serializable
    @Parcelize
    data class VideoEdit(val chatId: Long, val uri: String) : Route

    @Serializable
    @Parcelize
    data class VideoPlayer(val uri: String) : Route

    @Serializable
    @Parcelize
    data class Profile(val userId: String) : Route

    @Serializable
    @Parcelize
    data class PostDetail(val postId: String) : Route
}

enum class TopLevelDestination(
    val route: Route,
    val label: StringResource,
    val imageVector: ImageVector
) {
    Timeline(
        route = Route.TimelineTab,
        label = Res.string.timeline,
        imageVector = Icons.Outlined.VideoLibrary,
    ),
    ChatsList(
        route = Route.ChatsListTab,
        label = Res.string.chats,
        imageVector = Icons.Outlined.ChatBubbleOutline,
    ),
    Settings(
        route = Route.SettingsTab,
        label = Res.string.settings,
        imageVector = Icons.Outlined.Settings,
    ),
    ;

    companion object {
        val START_DESTINATION = ChatsList

        fun fromNavDestination(destination: NavDestination?): TopLevelDestination {
            return entries.find { dest ->
                destination?.hierarchy?.any {
                    it.hasRoute(dest.route::class)
                } == true
            } ?: START_DESTINATION
        }

        fun NavDestination.isTopLevel(): Boolean {
            return entries.any {
                hasRoute(it.route::class)
            }
        }
    }
}

@Composable
fun SocialiteNavSuite(
    modifier: Modifier = Modifier,
    currentRoute: Route,
    onTabSelected: (Route) -> Unit,
    content: @Composable () -> Unit,
) {
    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach {
                val isSelected = it.route == currentRoute
                item(
                    selected = isSelected,
                    onClick = {
                        onTabSelected(it.route)
                    },
                    icon = {
                        Icon(
                            imageVector = it.imageVector,
                            contentDescription = stringResource(it.label)
                        )
                    },
                    label = {
                        Text(text = stringResource(it.label))
                    },
                    alwaysShowLabel = false,
                )
            }
        }
    ) {
        content()
    }
}