package com.kmkole86.data.data_source

import com.kmkole86.data.model.PageData

interface MovieRemoteDataSource {

    suspend fun fetchPages(pageOrdinals: List<Int>): List<PageData>
}