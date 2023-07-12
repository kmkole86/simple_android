package com.kmkole86.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kmkole86.data.local.model.MovieLocal
import com.kmkole86.data.local.model.PageLocal
import com.kmkole86.data.local.model.PopulatedPageLocal
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun _insertMovies(movies: List<MovieLocal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun _insertPages(pages: List<PageLocal>)

    @Transaction
    open suspend fun insert(value: List<PopulatedPageLocal>) {
        _insertMovies(value.flatMap { it.movies })
        _insertPages(value.map { it.page })
    }

    @Query("DELETE FROM ${PageLocal.ENTITY_NAME} WHERE ${PageLocal.ORDINAL} = :pageOrdinal")
    abstract suspend fun _deletePage(pageOrdinal: Int)

    @Query("DELETE FROM ${MovieLocal.ENTITY_NAME} WHERE ${MovieLocal.ID} = :pageOrdinal")
    abstract suspend fun _deleteMoviesForPage(pageOrdinal: Int)

    @Transaction
    open suspend fun deletePage(pageOrdinal: Int) {
        _deleteMoviesForPage(pageOrdinal)
        _deletePage(pageOrdinal)
    }

    @Query("SELECT ${PageLocal.ORDINAL} FROM ${PageLocal.ENTITY_NAME} WHERE ${PageLocal.ORDINAL} >= :fromInclusive AND ${PageLocal.ORDINAL} < :toExclusive")
    abstract suspend fun getCachedRangeWithinLimits(
        fromInclusive: Int,
        toExclusive: Int
    ): List<Int>

    /**
     * cant use "between" since its both limits are inclusive
     */
    @Transaction
    @Query("SELECT * FROM ${PageLocal.ENTITY_NAME} WHERE ${PageLocal.ORDINAL} >= :fromInclusive AND ${PageLocal.ORDINAL} < :toExclusive")
    abstract fun observeCachedPages(
        fromInclusive: Int,
        toExclusive: Int
    ): Flow<List<PopulatedPageLocal>>

    @Transaction
    @Query("SELECT * FROM ${PageLocal.ENTITY_NAME} WHERE ${PageLocal.ORDINAL} >= :fromInclusive AND ${PageLocal.ORDINAL} < :toExclusive")
    abstract fun getCachedPages(
        fromInclusive: Int,
        toExclusive: Int
    ): List<PopulatedPageLocal>
}