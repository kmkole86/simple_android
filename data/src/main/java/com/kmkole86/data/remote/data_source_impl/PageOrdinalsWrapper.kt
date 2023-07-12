package com.kmkole86.data.remote.data_source_impl

import com.kmkole86.data.data_source.MovieRemoteDataSource
import com.kmkole86.data.model.PageData
import javax.inject.Inject

class PageOrdinalsWrapper @Inject constructor(private val remoteDataSource: MovieRemoteDataSource) :
    MovieRemoteDataSource {

    override suspend fun fetchPages(pageOrdinals: List<Int>): List<PageData> {
        return remoteDataSource.fetchPages(pageOrdinals.map { it + 1 })
            .map { page -> page.copy(ordinal = page.ordinal - 1) }
    }
}