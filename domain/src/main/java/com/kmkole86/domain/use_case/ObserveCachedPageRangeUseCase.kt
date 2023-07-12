package com.kmkole86.domain.use_case

import com.kmkole86.domain.entity.Page
import com.kmkole86.domain.entity.Range
import com.kmkole86.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class ObserveCachedPageRangeUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val repository: MovieRepository
) {

    fun observe(range: Range): Flow<List<Page>> {
        return repository.observeCachedPageRange(range).flowOn(dispatcher)
    }
}