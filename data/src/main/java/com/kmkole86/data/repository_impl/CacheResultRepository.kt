package com.kmkole86.data.repository_impl

import com.kmkole86.data.data_source.MovieLocalDataSource
import com.kmkole86.domain.entity.Page
import com.kmkole86.domain.entity.Range
import com.kmkole86.domain.repository.MovieRepository
import com.kmkole86.domain.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Decorator patter,
 * adding behaviour of caching downloaded values
 * as a separate shell around the MovieRepository
 */
class CacheResultRepository @Inject constructor(
    private val localDataSource: MovieLocalDataSource,
    private val repository: MovieRepository
) : MovieRepository {

    override suspend fun getRange(range: Range): Result<List<Page>> {
        return when (val result = repository.getRange(range)) {
            is Result.Error -> {
                Result.Error()
            }

            is Result.Success -> {
                localDataSource.insertPages(result.data.map { MovieRepositoryMapper.mapToPageData(it) })
                Result.Success(localDataSource.getCachedPageRange(range)
                    .map { MovieRepositoryMapper.mapToPage(it) })
            }
        }
    }

    override fun observeCachedPageRange(range: Range): Flow<List<Page>> {
        return repository.observeCachedPageRange(range)
    }
}