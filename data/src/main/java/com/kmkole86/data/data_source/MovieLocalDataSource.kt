package com.kmkole86.data.data_source

import com.kmkole86.data.model.PageData
import com.kmkole86.domain.entity.Range
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {

    suspend fun insertPages(pages: List<PageData>)

    suspend fun deletePage(pageOrdinal: Int)

    suspend fun getCachedRangeWithinLimits(range: Range): Range

    suspend fun isRangeCached(range: Range): Boolean

    fun getCachedPageRange(range: Range): List<PageData>
    fun observeCachedPageRange(range: Range): Flow<List<PageData>>
}