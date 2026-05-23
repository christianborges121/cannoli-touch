package dev.cannoli.scorza.navigation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigation = staticCompositionLocalOf<NavigationController> {
    error("NavigationController not provided. Wrap setContent in CompositionLocalProvider.")
}
