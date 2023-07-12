package com.kmkole86.data.local.data_source_impl

import com.kmkole86.data.data_source.MovieLocalDataSource
import com.kmkole86.data.local.dao.MovieDao
import com.kmkole86.data.local.model.MovieLocal
import com.kmkole86.data.local.model.PageLocal
import com.kmkole86.data.local.model.PopulatedPageLocal
import com.kmkole86.data.model.MovieData
import com.kmkole86.data.model.PageData
import com.kmkole86.domain.entity.Range
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieLocalDataSourceImpl @Inject constructor(private val dao: MovieDao) :
    MovieLocalDataSource {
    override suspend fun insertPages(pages: List<PageData>) {
        dao.insert(MovieLocalMapper.mapToPopulatedPagesLocal(pages))
    }

    override suspend fun getCachedRangeWithinLimits(range: Range): Range {
        val ordinals: List<Int> = dao.getCachedRangeWithinLimits(
            fromInclusive = range.fromInclusive,
            toExclusive = range.toExclusive
        ).sorted()

        return Range(
            fromInclusive = ordinals.firstOrNull() ?: 0,
            toExclusive = ordinals.lastOrNull() ?: 0
        )
    }

    override suspend fun isRangeCached(range: Range): Boolean {
        val cached = getCachedRangeWithinLimits(range)
        println(cached)
        return range == getCachedRangeWithinLimits(range)
    }

    override fun observeCachedPageRange(range: Range): Flow<List<PageData>> {
        return dao.observeCachedPages(
            fromInclusive = range.fromInclusive,
            toExclusive = range.toExclusive
        )
            .distinctUntilChanged()
            .map { pageList -> pageList.map { MovieLocalMapper.mapToPageData(it) } }
    }

    override suspend fun deletePage(pageOrdinal: Int) {
        dao.deletePage(pageOrdinal)
    }

    override fun getCachedPageRange(range: Range): List<PageData> {
        return dao.getCachedPages(
            fromInclusive = range.fromInclusive,
            toExclusive = range.toExclusive
        ).map { MovieLocalMapper.mapToPageData(it) }
    }
}


object MovieLocalMapper {

    fun mapToPopulatedPagesLocal(values: List<PageData>): List<PopulatedPageLocal> {
        return with(values) {
            values.map { page ->
                PopulatedPageLocal(
                    page = mapToPageLocal(page),
                    movies = page.movies.map { movie ->
                        mapToMovieLocal(
                            value = movie,
                            pageOrdinal = page.ordinal
                        )
                    })
            }
        }
    }

    fun mapToPageLocal(value: PageData): PageLocal {
        return with(value) {
            PageLocal(
                ordinal = ordinal,
                totalPages = totalPages,
                totalResults = totalResults
            )
        }
    }

    fun mapToMovieLocal(value: MovieData, pageOrdinal: Int): MovieLocal {
        return with(value) {
            MovieLocal(
                id = id,
                title = title,
                overview = overview,
                posterPath = posterPath,
                releaseDate = releaseDate,
                voteAverage = voteAverage,
                voteCount = voteCount,
                pageOrdinal = pageOrdinal
            )
        }
    }

    fun mapToPageData(value: PopulatedPageLocal): PageData {
        return with(value) {
            PageData(
                ordinal = page.ordinal,
                totalPages = page.totalPages,
                totalResults = page.totalResults,
                movies = movies.map { mapToMovieData(it) }
            )
        }
    }

    fun mapToPageDataNullable(value: PopulatedPageLocal?): PageData? {
        return value?.let {
            with(value) {
                PageData(
                    ordinal = page.ordinal,
                    totalPages = page.totalPages,
                    totalResults = page.totalResults,
                    movies = movies.map { movie -> mapToMovieData(movie) }
                )
            }
        }


    }

    fun mapToMovieData(value: MovieLocal): MovieData {
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

