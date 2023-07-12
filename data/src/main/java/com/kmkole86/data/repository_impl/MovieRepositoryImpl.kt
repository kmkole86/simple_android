package com.kmkole86.data.repository_impl

import com.kmkole86.data.common.RangeUtils
import com.kmkole86.data.common.runCatchingCancelable
import com.kmkole86.data.data_source.MovieLocalDataSource
import com.kmkole86.data.data_source.MovieRemoteDataSource
import com.kmkole86.data.di.MovieRepo
import com.kmkole86.data.model.MovieData
import com.kmkole86.data.model.PageData
import com.kmkole86.domain.entity.Movie
import com.kmkole86.domain.entity.Page
import com.kmkole86.domain.entity.Range
import com.kmkole86.domain.repository.MovieRepository
import com.kmkole86.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class MovieRepositoryImpl @Inject constructor(
    private val localDataSource: MovieLocalDataSource,
    private val remoteDataSource: MovieRemoteDataSource
) : MovieRepository {

    override suspend fun getRange(range: Range): Result<List<Page>> {
        return runCatchingCancelable {
            remoteDataSource.fetchPages(RangeUtils.pageOrdinals(range))
        }.fold(onSuccess = { pages ->
            Result.Success(pages.map {
                MovieRepositoryMapper.mapToPage(it)
            })
        }, onFailure = {
            Result.Error(it)
        })
    }

    override fun observeCachedPageRange(range: Range): Flow<List<Page>> {
        return localDataSource.observeCachedPageRange(range)
            .map { pages -> pages.map { page -> MovieRepositoryMapper.mapToPage(page) } }
    }
}

object MovieRepositoryMapper {

    fun mapToPageData(value: Page): PageData {
        return with(value) {
            PageData(
                ordinal = ordinal,
                totalPages = totalPages,
                totalResults = totalResults,
                movies = movies.map { mapToMovieData(it) }
            )
        }
    }

    fun mapToMovieData(value: Movie): MovieData {
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

    fun mapToPage(value: PageData): Page {
        return with(value) {
            Page(
                ordinal = ordinal,
                totalPages = totalPages,
                totalResults = totalResults,
                movies = movies.map { mapToMovie(it) }
            )
        }
    }

    fun mapToPageNullable(value: PageData?): Page? {
        return value?.let {
            with(it) {
                Page(
                    ordinal = ordinal,
                    totalPages = totalPages,
                    totalResults = totalResults,
                    movies = movies.map { movie -> mapToMovie(movie) }
                )
            }
        }
    }

    fun mapToMovie(value: MovieData): Movie {
        return with(value) {
            Movie(
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