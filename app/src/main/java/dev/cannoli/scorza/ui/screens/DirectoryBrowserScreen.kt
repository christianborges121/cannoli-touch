package dev.cannoli.scorza.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import dev.cannoli.scorza.R
import dev.cannoli.scorza.ui.components.ListDialogScreen
import dev.cannoli.ui.ButtonStyle
import dev.cannoli.ui.components.List
import dev.cannoli.scorza.input.screen.compose.LocalScreenInputRegistry
import dev.cannoli.scorza.navigation.LauncherScreen
import dev.cannoli.scorza.navigation.LocalNavigation
import dev.cannoli.scorza.navigation.BrowsePurpose
import dev.cannoli.ui.components.PillRowText

@Composable
fun DirectoryBrowserScreen(
    currentPath: String,
    entries: List<String>,
    selectedIndex: Int,
    scrollTarget: Int,
    backgroundImagePath: String?,
    backgroundTint: Int,
    listFontSize: TextUnit,
    listLineHeight: TextUnit,
    listVerticalPadding: Dp,
    itemHeight: Dp,
    isSelectRow: Boolean,
    showSelectOption: Boolean = true,
    showNewFolder: Boolean = true,
    onListStateChanged: ((androidx.compose.foundation.lazy.LazyListState?) -> Unit)?,
    onItemClicked: ((Int) -> Unit)? = null,
    buttonStyle: ButtonStyle
) {
    val displayItems = if (showSelectOption) listOf(stringResource(R.string.label_use_location)) + entries else entries

    val rightItems = buildList {
        if (showNewFolder && showSelectOption) add(buttonStyle.north to stringResource(R.string.label_new_folder))
        if (showSelectOption && isSelectRow) {
            add(buttonStyle.confirm to stringResource(R.string.label_select))
        } else {
            add(buttonStyle.confirm to stringResource(R.string.label_open))
        }
    }

    ListDialogScreen(
        backgroundImagePath = backgroundImagePath,
        backgroundTint = backgroundTint,
        title = currentPath,
        listFontSize = listFontSize,
        listLineHeight = listLineHeight,
        fullWidth = true,
        showBackButton = false,
        leftBottomItems = buildList {
            add(buttonStyle.west to stringResource(R.string.label_cancel))
            if (showSelectOption) add(buttonStyle.back to stringResource(R.string.label_parent))
        },
        rightBottomItems = rightItems,
        buttonStyle = buttonStyle
    ) {
        val registry = LocalScreenInputRegistry.current
        val nav = LocalNavigation.current
        List(
                items = displayItems,
                selectedIndex = selectedIndex,
                itemHeight = itemHeight,
                scrollTarget = scrollTarget,
                onListStateChanged = onListStateChanged
            ) { idx, item, isSelected ->
                PillRowText(
                    label = item,
                    isSelected = isSelected,
                    fontSize = listFontSize,
                    lineHeight = listLineHeight,
                    verticalPadding = listVerticalPadding,
                    modifier = Modifier.clickable {
                        if (!isSelected) {
                            if (onItemClicked != null) onItemClicked(idx) else {
                                // fallback: try to replace top if possible
                                try {
                                    nav.replaceTop(LauncherScreen.DirectoryBrowser(BrowsePurpose.FILES, currentPath = currentPath, entries = entries, selectedIndex = idx, scrollTarget = scrollTarget))
                                } catch (_: Exception) {
                                    // ignore if we can't reconstruct
                                }
                            }
                        } else registry.top.onConfirm()
                    }
                )
            }
    }
}
