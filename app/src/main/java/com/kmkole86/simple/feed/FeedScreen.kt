package com.kmkole86.simple.feed

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun FeedRoute(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val moviesState by viewModel.stateObservable.collectAsStateWithLifecycle()

    MoviesScreen(
        modifier = modifier,
        feedItems = moviesState.uiItems,
        onMovieClicked = {
            //TODO("navigate to details")
        },
        onBottomOfListReached = viewModel::onEvent,
        onRetryClicked = viewModel::onEvent
    )
}

@Composable
internal fun MoviesScreen(
    modifier: Modifier = Modifier,
    feedItems: ImmutableList<FeedUiItem>,
    onMovieClicked: (Int) -> Unit,
    onBottomOfListReached: (FeedViewModel.FeedEvent) -> Unit,
    onRetryClicked: (FeedViewModel.FeedEvent) -> Unit
) {

    val listState = rememberLazyListState()

    listState.OnBottomReached {
        onBottomOfListReached(FeedViewModel.FeedEvent.OnBottomOfListReached)
    }

    MovieList(
        lazyListState = listState,
        feedItems = feedItems,
        onMovieClicked = onMovieClicked,
        onRetryClicked = onRetryClicked,
        modifier = modifier
    )
}