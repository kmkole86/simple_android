package com.kmkole86.simple.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kmkole86.simple.R
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MovieList(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    feedItems: ImmutableList<FeedUiItem>,
    onMovieClicked: (Int) -> Unit,
    onRetryClicked: (FeedViewModel.FeedEvent) -> Unit,
) {

    LazyColumn(
        state = lazyListState, modifier = modifier.fillMaxSize()
    ) {
        items(feedItems.size, key = { index -> feedItems[index].key }, itemContent = { index ->
            when (val item = feedItems[index]) {
                FeedUiItem.Loading -> {
                    LoadingListItem(modifier = modifier)
                }

                is FeedUiItem.Movie -> {
                    MovieListItem(
                        movie = item, onItemClicked = onMovieClicked
                    )
                }

                FeedUiItem.Error -> {
                    ErrorListItem(modifier = modifier, onRetryClicked = onRetryClicked)
                }
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListItem(
    movie: FeedUiItem.Movie, onItemClicked: (Int) -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(
                vertical = dimensionResource(id = R.dimen.spacing_1x),
                horizontal = dimensionResource(id = R.dimen.spacing_2x)
            )
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.spacing_12x)),
        onClick = {
            onItemClicked(movie.id)
        },
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            text = movie.title,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview
fun LoadingListItem(modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            text = "Loading",
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        LinearProgressIndicator(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
fun ErrorListItem(
    onRetryClicked: (FeedViewModel.FeedEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(modifier = modifier) {
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                text = "Something went wrong...",
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Button(modifier = modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
                onClick = { onRetryClicked(FeedViewModel.FeedEvent.OnRetryClicked) }) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
fun LazyListState.OnBottomReached(
    onBottomReached: (FeedViewModel.FeedEvent) -> Unit
) {

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false

            lastVisibleItem.index > layoutInfo.totalItemsCount - 6
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }.collect {
            // if should load more, then invoke loadMore
            if (it) {
                onBottomReached(FeedViewModel.FeedEvent.OnBottomOfListReached)
            }
        }
    }
}

@Immutable
sealed class FeedUiItem {
    abstract val key: Int

    data class Movie(
        val id: Int,
        val title: String,
        val overview: String,
        val posterPath: String,
        val releaseDate: String,
        val voteAverage: Float,
        val voteCount: Int
    ) : FeedUiItem() {
        override val key: Int
            get() = id

        companion object {
            fun createFrom(item: com.kmkole86.domain.entity.Movie): FeedUiItem.Movie {
                return Movie(
                    id = item.id,
                    title = item.title,
                    overview = item.overview,
                    posterPath = item.posterPath,
                    releaseDate = item.releaseDate,
                    voteAverage = item.voteAverage,
                    voteCount = item.voteCount
                )
            }
        }
    }

    object Loading : FeedUiItem() {
        override val key: Int
            get() = -1
    }

    object Error : FeedUiItem() {
        override val key: Int
            get() = -2
    }
}