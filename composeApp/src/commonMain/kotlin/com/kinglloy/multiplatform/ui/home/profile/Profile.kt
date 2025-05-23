package com.kinglloy.multiplatform.ui.home.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.defaultDragHandleSemantics
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color // If not already imported
// import androidx.compose.ui.unit.dp // Already imported via androidx.compose.ui.unit.dp
import com.kinglloy.multiplatform.ui.VerticalDragHandle
import com.kinglloy.multiplatform.ui.components.videoplayer.VideoPlayer
import com.kinglloy.multiplatform.ui.components.videoplayer.VideoPlayerController
import kotlin_multiplatform_playground.composeapp.generated.resources.Res
import kotlin_multiplatform_playground.composeapp.generated.resources.ic_food
import kotlin_multiplatform_playground.composeapp.generated.resources.ic_no_food
import kotlin_multiplatform_playground.composeapp.generated.resources.placeholder
import kotlin_multiplatform_playground.composeapp.generated.resources.settings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val loremIpsum = """
        |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Dui nunc mattis enim ut tellus elementum sagittis. Nunc sed augue lacus viverra vitae. Sit amet dictum sit amet justo donec. Fringilla urna porttitor rhoncus dolor purus non enim praesent elementum. Dictum non consectetur a erat nam at lectus urna. Tellus mauris a diam maecenas sed enim ut sem viverra. Commodo ullamcorper a lacus vestibulum sed arcu non. Lorem mollis aliquam ut porttitor leo a diam sollicitudin tempor. Pellentesque habitant morbi tristique senectus et netus et malesuada. Vitae suscipit tellus mauris a diam maecenas sed. Neque ornare aenean euismod elementum nisi quis. Quam vulputate dignissim suspendisse in est ante in nibh mauris. Tellus in metus vulputate eu scelerisque felis imperdiet proin fermentum. Orci ac auctor augue mauris augue neque gravida.
        |
        |Tempus quam pellentesque nec nam aliquam. Praesent semper feugiat nibh sed. Adipiscing elit duis tristique sollicitudin nibh sit. Netus et malesuada fames ac turpis egestas sed tempus urna. Quis varius quam quisque id diam vel quam. Urna duis convallis convallis tellus id interdum velit laoreet. Id eu nisl nunc mi ipsum. Fermentum dui faucibus in ornare. Nunc lobortis mattis aliquam faucibus. Vulputate mi sit amet mauris commodo quis. Porta nibh venenatis cras sed. Vitae tortor condimentum lacinia quis vel eros donec. Eu non diam phasellus vestibulum.
        """.trimMargin()
private val sampleWords = listOf(
    "Apple" to Res.drawable.ic_food,
    "Banana" to Res.drawable.ic_no_food,
    "Cherry" to Res.drawable.ic_food,
    "Date" to Res.drawable.ic_no_food,
    "Elderberry" to Res.drawable.ic_food,
    "Fig" to Res.drawable.ic_no_food,
    "Grape" to Res.drawable.ic_food,
    "Honeydew" to Res.drawable.ic_no_food,
).map { (word, icon) -> DefinedWord(word, icon) }

private data class DefinedWord(
    val word: String,
    val icon: DrawableResource,
    val definition: String = loremIpsum
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Profile(
    userId: String,
    onProfileClicked: (userId: String) -> Unit,
    onDetailClicked: (postId: String) -> Unit,
    onCameraClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedWordIndex: Int? by rememberSaveable { mutableStateOf(null) }
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val scope = rememberCoroutineScope()
    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded
                && navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

    BackHandler(enabled = navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }
    SharedTransitionLayout {
        AnimatedContent(targetState = isListAndDetailVisible, label = "simple sample") {
            ListDetailPaneScaffold(
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    val currentSelectedWordIndex = selectedWordIndex
                    val isDetailVisible =
                        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded
                    AnimatedPane {
                        ListContent(
                            userId = userId,
                            onProfileClicked = onProfileClicked,
                            onDetailClicked = onDetailClicked,
                            onCameraClicked = onCameraClicked,
                            words = sampleWords,
                            selectionState = if (isDetailVisible && currentSelectedWordIndex != null) {
                                SelectionVisibilityState.ShowSelection(currentSelectedWordIndex)
                            } else {
                                SelectionVisibilityState.NoSelection
                            },
                            onIndexClick = { index ->
                                selectedWordIndex = index
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                }
                            },
                            isListAndDetailVisible = isListAndDetailVisible,
                            isListVisible = !isDetailVisible,
                            animatedVisibilityScope = this@AnimatedPane,
                            sharedTransitionScope = this@SharedTransitionLayout
                        )
                    }
                },
                detailPane = {
                    val definedWord = selectedWordIndex?.let(sampleWords::get)
                    val isDetailVisible =
                        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded
                    AnimatedPane {
                        DetailContent(
                            definedWord = definedWord,
                            isListAndDetailVisible = isListAndDetailVisible,
                            isDetailVisible = isDetailVisible,
                            animatedVisibilityScope = this@AnimatedPane,
                            sharedTransitionScope = this@SharedTransitionLayout
                        )
                    }
                },
                paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
                paneExpansionDragHandle = { state ->
                    val interactionSource = remember { MutableInteractionSource() }
                    AnimatedVisibility(visible = isListAndDetailVisible) {
                        VerticalDragHandle(
                            modifier = Modifier.paneExpansionDraggable(
                                state,
                                LocalMinimumInteractiveComponentSize.current,
                                interactionSource,
                                state.defaultDragHandleSemantics()
                            ), interactionSource = interactionSource
                        )
                    }
                }
            )
        }
    }
}


/**
 * The description of the selection state for the [ListContent]
 */
sealed interface SelectionVisibilityState {

    /**
     * No selection should be shown, and each item should be clickable.
     */
    object NoSelection : SelectionVisibilityState

    /**
     * Selection state should be shown, and each item should be selectable.
     */
    data class ShowSelection(
        /**
         * The index of the word that is selected.
         */
        val selectedWordIndex: Int
    ) : SelectionVisibilityState
}

/**
 * The content for the list pane.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ListContent(
    userId: String,
    onProfileClicked: (userId: String) -> Unit,
    onDetailClicked: (postId: String) -> Unit,
    onCameraClicked: () -> Unit,
    words: List<DefinedWord>,
    selectionState: SelectionVisibilityState,
    onIndexClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    isListAndDetailVisible: Boolean,
    isListVisible: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .then(
                when (selectionState) {
                    SelectionVisibilityState.NoSelection -> Modifier
                    is SelectionVisibilityState.ShowSelection -> Modifier.selectableGroup()
                }
            )
    ) {
        item {
            Text(text = "Profile:$userId")
        }
        // --- Video Player Integration Start ---
        item { // Place it as a LazyColumn item
            val videoPlayerController = remember { VideoPlayerController() }
            val testVideoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

            VideoPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Adjust height as needed
                    .background(Color.Black), // Background for the player area
                url = testVideoUrl,
                controller = videoPlayerController
            )
        }
        // --- Video Player Integration End ---
        item {
            Button(onClick = { onProfileClicked("Profile:$userId - user#1") }) {
                Text(text = "Open Profile")
            }
        }
        item {
            Button(onClick = { onDetailClicked("Profile:$userId - post#1") }) {
                Text(text = "Open Detail")
            }
        }
        item {
            Button(onClick = onCameraClicked) {
                Text(text = "Open Camera")
            }
        }
        itemsIndexed(words) { index, word ->
            val interactionModifier = when (selectionState) {
                SelectionVisibilityState.NoSelection -> {
                    Modifier.clickable(
                        onClick = { onIndexClick(index) }
                    )
                }

                is SelectionVisibilityState.ShowSelection -> {
                    Modifier.selectable(
                        selected = index == selectionState.selectedWordIndex,
                        onClick = { onIndexClick(index) }
                    )
                }
            }

            val containerColor = when (selectionState) {
                SelectionVisibilityState.NoSelection -> MaterialTheme.colorScheme.surface
                is SelectionVisibilityState.ShowSelection ->
                    if (index == selectionState.selectedWordIndex) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
            }
            val borderStroke = when (selectionState) {
                SelectionVisibilityState.NoSelection -> BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                )

                is SelectionVisibilityState.ShowSelection ->
                    if (index == selectionState.selectedWordIndex) {
                        null
                    } else {
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )
                    }
            }

            // TODO: Card selection overfills the Card
            Card(
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = borderStroke,
                modifier = Modifier
                    .then(interactionModifier)
                    .fillMaxWidth()
            ) {
                Row {
                    val imageModifier = Modifier.padding(horizontal = 8.dp)
                    if (!isListAndDetailVisible && isListVisible) {
                        with(sharedTransitionScope) {
                            val state = rememberSharedContentState(key = word.word)
                            imageModifier.then(
                                Modifier.sharedElement(
                                    state,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            )
                        }
                    }

                    Image(
                        painter = painterResource(word.icon),
                        contentDescription = word.word,
                        modifier = imageModifier
                    )
                    Text(
                        text = word.word,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

            }
        }
    }
}

/**
 * The content for the detail pane.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailContent(
    definedWord: DefinedWord?,
    modifier: Modifier = Modifier,
    isListAndDetailVisible: Boolean,
    isDetailVisible: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        if (definedWord != null) {

            val imageModifier = Modifier
                .padding(horizontal = 8.dp)
                .then(
                    if (!isListAndDetailVisible && isDetailVisible) {
                        with(sharedTransitionScope) {
                            val state = rememberSharedContentState(key = definedWord.word)
                            Modifier.sharedElement(
                                state,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    } else {
                        Modifier
                    }
                )

            Image(
                painter = painterResource(definedWord.icon),
                contentDescription = definedWord.word,
                modifier = imageModifier
            )
            Text(
                text = definedWord.word,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = definedWord.definition
            )
        } else {
            Text(
                text = stringResource(Res.string.placeholder)
            )
        }
    }
}

