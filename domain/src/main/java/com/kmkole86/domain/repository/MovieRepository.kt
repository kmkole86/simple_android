package com.kmkole86.domain.repository

import com.kmkole86.domain.entity.Page
import com.kmkole86.domain.entity.Range
import com.kmkole86.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun getRange(range: Range): Result<List<Page>>

    fun observeCachedPageRange(range: Range): Flow<List<Page>>
}