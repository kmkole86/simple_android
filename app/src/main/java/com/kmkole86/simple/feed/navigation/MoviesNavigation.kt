package com.kmkole86.simple.feed.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.kmkole86.simple.feed.FeedRoute

const val feedRoute = "feed_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.feedScreen() {
    composable(route = feedRoute, enterTransition = {
        slideIntoContainer(
            AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700)
        )
    }, exitTransition = {
        slideOutOfContainer(
            AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700)
        )
    }) {
        FeedRoute()
    }
}