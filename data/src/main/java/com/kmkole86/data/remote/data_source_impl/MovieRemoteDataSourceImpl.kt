package com.kmkole86.data.remote.data_source_impl

import com.kmkole86.data.data_source.MovieRemoteDataSource
import com.kmkole86.data.model.MovieData
import com.kmkole86.data.model.PageData
import com.kmkole86.data.remote.model.MovieResponse
import com.kmkole86.data.remote.model.PageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

const val PAGE_PARAMETER_KEY = "page"

class MovieRemoteDataSourceImpl @Inject constructor(private val client: HttpClient) :
    MovieRemoteDataSource {

    override suspend fun fetchPages(pageOrdinals: List<Int>): List<PageData> = coroutineScope {
        pageOrdinals.map<Int, Deferred<PageResponse>> { ordinal ->
            async {
                client.get("top_rated") {
                    url {
                        parameters.append(PAGE_PARAMETER_KEY, ordinal.toString())
                    }
                }.body<PageResponse>()
            }
        }.awaitAll().map {
            MovieResponseMapper.mapToPageData(it)
        }
    }
}

object MovieResponseMapper {

    fun mapToPageData(value: PageResponse): PageData {
        return with(value) {
            PageData(
                ordinal = ordinal,
                totalPages = totalPages,
                totalResults = totalResults,
                movies = movies.map { mapToMovieData(it) }
            )
        }
    }

    fun mapToMovieData(value: MovieResponse): MovieData {
        return with(value) {
            MovieData(
                id = id,
                title = title,
                overview = overview,
                posterPath = posterPath,
                releaseDate = releaseDate,
                voteAverage = voteAverage,
                voteCount = voteCount
            )
        }
    }
}
