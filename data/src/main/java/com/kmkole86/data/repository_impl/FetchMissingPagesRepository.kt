package com.kmkole86.data.repository_impl

import com.kmkole86.data.common.RangeUtils
import com.kmkole86.data.data_source.MovieLocalDataSource
import com.kmkole86.domain.entity.Page
import com.kmkole86.domain.entity.Range
import com.kmkole86.domain.repository.MovieRepository
import com.kmkole86.domain.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchMissingPagesRepository @Inject constructor(
    private val localDataSource: MovieLocalDataSource, private val repository: MovieRepository
) : MovieRepository {

    override suspend fun getRange(range: Range): Result<List<Page>> {
        val cachedRange: Range = localDataSource.getCachedRangeWithinLimits(range)

        val missingRange: Range =
            RangeUtils.rangeDifference(range1 = range, range2 = cachedRange)

        return repository.getRange(missingRange)
    }

    override fun observeCachedPageRange(range: Range): Flow<List<Page>> {
        return repository.observeCachedPageRange(range)
    }
}