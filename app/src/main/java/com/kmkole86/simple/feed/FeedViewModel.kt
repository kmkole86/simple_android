package com.kmkole86.simple.feed

import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmkole86.domain.entity.Page
import com.kmkole86.domain.entity.Range
import com.kmkole86.domain.entity.hasNext
import com.kmkole86.domain.use_case.GetPageUseCase
import com.kmkole86.domain.use_case.ObserveCachedPageRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getPageUseCase: GetPageUseCase,
    private val observeCachedRangeUseCase: ObserveCachedPageRangeUseCase,
    private val uiItemsMapper: FeedItemsMapper
) : ViewModel() {

    private var observeRangeJob: Job = Job()

    private val _stateObservable: MutableStateFlow<FeedState> = MutableStateFlow(
        FeedState.empty()
    )
    val stateObservable: StateFlow<FeedState> = _stateObservable.asStateFlow()

    init {
        //TODO ("save restore state using savedStateHandle")
        //open initial page if there is no requirements for other e.g. 10th
        getPages(stateObservable.value.range.copy(toExclusive = 1))
    }

    private fun getPages(range: Range) {
        viewModelScope.launch {
            getPageUseCase.get(range = range).collect { result ->
                when (result) {
                    GetPageUseCase.PageResult.InFlight -> onAction(FeedAction.OnFetchPageInFlight)
                    GetPageUseCase.PageResult.Error -> onAction(FeedAction.OnFetchPageError)
                    is GetPageUseCase.PageResult.Success -> {
                        onEvent(FeedEvent.OnRangeCached(range = result.range))
                    }
                }
            }
        }
    }

    private fun observeCachedPages(range: Range) {
        observeRangeJob.cancel()
        observeRangeJob = viewModelScope.launch {
            observeCachedRangeUseCase.observe(range)
                .collect { result -> onAction(FeedAction.OnObserveRangeResult(payload = result)) }
        }
    }

    /**
     * onEvent=> place where operations are triggered
     * can read current state but can not change it
     */
    fun onEvent(event: FeedEvent) {
        when (event) {
            FeedEvent.OnBottomOfListReached -> {
                if (stateObservable.value is FeedState.Fetched)
                    getPages(stateObservable.value.range.copy(toExclusive = stateObservable.value.range.toExclusive + 1))
            }

            is FeedEvent.OnRangeCached -> observeCachedPages(range = event.range)

            FeedEvent.OnRetryClicked -> getPages(stateObservable.value.range.copy(toExclusive = stateObservable.value.range.toExclusive + 1))
        }
    }

    /**
     * onAction => the place where state is calculated
     * and updated
     */
    @MainThread
    private fun onAction(action: FeedAction) {
        val newState: FeedState = when (action) {
            FeedAction.OnFetchPageError -> {
                FeedState.Error(
                    items = stateObservable.value.items,
                    uiItems = uiItemsMapper.generateErrorMovieItems(stateObservable.value.items)
                        .toImmutableList(),
                    range = stateObservable.value.range
                )
            }

            FeedAction.OnFetchPageInFlight -> {
                FeedState.FetchInFlight(
                    items = stateObservable.value.items,
                    uiItems = uiItemsMapper.generateLoadingMovieItems(stateObservable.value.items)
                        .toImmutableList(),
                    range = stateObservable.value.range
                )
            }

            is FeedAction.OnObserveRangeResult -> {
                val newItems =
                    action.payload.sortedWith(compareBy { it.ordinal }).toPersistentList()
                if (newItems.lastOrNull()?.hasNext != false)
                    FeedState.Fetched(
                        items = newItems,
                        uiItems = uiItemsMapper.generateMovieItems(newItems).toImmutableList(),
                        range = Range(
                            fromInclusive = newItems.firstOrNull()?.ordinal ?: 0,
                            toExclusive = (newItems.lastOrNull()?.ordinal ?: 0) + 1
                        )
                    )
                else
                    FeedState.EndOfList(
                        items = newItems,
                        uiItems = uiItemsMapper.generateMovieItems(newItems).toImmutableList(),
                        range = Range(
                            fromInclusive = newItems.firstOrNull()?.ordinal ?: 0,
                            toExclusive = (newItems.lastOrNull()?.ordinal ?: 0) + 1
                        )
                    )
            }
        }
        _stateObservable.update { newState }
    }


    sealed class FeedEvent {
        data class OnRangeCached(val range: Range) : FeedEvent()
        object OnBottomOfListReached : FeedEvent()
        object OnRetryClicked : FeedEvent()
    }

    sealed interface FeedAction {

        object OnFetchPageInFlight : FeedAction
        object OnFetchPageError : FeedAction
        data class OnObserveRangeResult(val payload: List<Page>) :
            FeedAction
    }

    sealed class FeedState {
        abstract val items: ImmutableList<Page>
        abstract val uiItems: ImmutableList<FeedUiItem>
        abstract val range: Range

        data class FetchInFlight(
            override val items: ImmutableList<Page>,
            override val uiItems: ImmutableList<FeedUiItem>,
            override val range: Range
        ) : FeedState()

        data class Fetched(
            override val items: ImmutableList<Page>,
            override val uiItems: ImmutableList<FeedUiItem>,
            override val range: Range
        ) : FeedState()

        data class Error(
            override val items: ImmutableList<Page>,
            override val uiItems: ImmutableList<FeedUiItem>,
            override val range: Range
        ) : FeedState()

        data class EndOfList(
            override val items: ImmutableList<Page>,
            override val uiItems: ImmutableList<FeedUiItem>,
            override val range: Range
        ) : FeedState()

        companion object {
            fun empty(): FeedState = Fetched(
                items = persistentListOf(),
                uiItems = persistentListOf(),
                range = Range(0, 0)
            )
        }
    }
}

class FeedItemsMapper @Inject constructor() {
    fun generateMovieItems(pages: List<Page>): List<FeedUiItem> {
        return pages.flatMap { it.movies }.map { FeedUiItem.Movie.createFrom(it) }
    }

    fun generateLoadingMovieItems(pages: List<Page>): List<FeedUiItem> {
        val items: MutableList<FeedUiItem> = generateMovieItems(pages = pages).toMutableList()
        items.add(FeedUiItem.Loading)
        return items
    }

    fun generateErrorMovieItems(pages: List<Page>): List<FeedUiItem> {
        val items: MutableList<FeedUiItem> = generateMovieItems(pages = pages).toMutableList()
        items.add(FeedUiItem.Error)
        return items
    }
}