package com.kmkole86.simple.main.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.kmkole86.simple.feed.navigation.feedRoute
import com.kmkole86.simple.feed.navigation.feedScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = feedRoute
) {
    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        feedScreen()
    }
}