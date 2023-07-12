package com.kmkole86.simple.main.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.kmkole86.simple.R
import com.kmkole86.simple.feed.navigation.feedRoute

sealed class TopDestinations(
    val route: String,
    @StringRes val resourceId: Int,
    val iconVector: ImageVector
) {
    object Feed : TopDestinations(
        route = feedRoute,
        resourceId = R.string.home_bottom_navigation_bar_feed,
        iconVector = Icons.Filled.Home
    )
}