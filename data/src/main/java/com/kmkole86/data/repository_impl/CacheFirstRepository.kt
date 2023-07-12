package com.kmkole86.data.repository_impl

import com.kmkole86.data.data_source.MovieLocalDataSource
import com.kmkole86.domain.entity.Page
import com.kmkole86.domain.entity.Range
import com.kmkole86.domain.repository.MovieRepository
import com.kmkole86.domain.result.Result
import kotlinx.coroutines.flow.Flow

/**
 * Proxy pattern
 * Returns cached value or
 * let the layer under to handle request
 * if page is not cached
 */
class CacheFirstRepository(
    private val localDataSource: MovieLocalDataSource,
    private val repository: MovieRepository
) : MovieRepository {

    override suspend fun getRange(range: Range): Result<List<Page>> {
        if (localDataSource.isRangeCached(range))
            return Result.Success(
                localDataSource.getCachedPageRange(range)
                    .map { MovieRepositoryMapper.mapToPage(it) })

        return repository.getRange(range)
    }

    override fun observeCachedPageRange(range: Range): Flow<List<Page>> {
        return repository.observeCachedPageRange(range)
    }
}