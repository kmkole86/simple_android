package com.kmkole86.domain.use_case

import com.kmkole86.domain.entity.Page
import com.kmkole86.domain.entity.Range
import com.kmkole86.domain.repository.MovieRepository
import com.kmkole86.domain.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class GetPageUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val repository: MovieRepository
) {

    fun get(range: Range): Flow<PageResult> =
        flow<PageResult> {
            when (val result = repository.getRange(range)) {
                is Result.Error -> {
                    emit(PageResult.Error)
                }

                is Result.Success -> {
                    emit(PageResult.Success(range = getRangeFromPageOrdinals(result.data)))
                }
            }
        }.onStart { emit(PageResult.InFlight) }.flowOn(dispatcher)

    private fun getRangeFromPageOrdinals(pages: List<Page>): Range {
        val sorted = pages.map { it.ordinal }.sortedBy { it }
        return Range(
            fromInclusive = sorted.firstOrNull() ?: 0,
            toExclusive = sorted.lastOrNull()?.let { it + 1 } ?: 0
        )
    }


    sealed interface PageResult {
        object InFlight : PageResult
        data class Success(val range: Range) : PageResult
        object Error : PageResult   //TODO("add meaningful errors")
    }
}