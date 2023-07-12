package com.kmkole86.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = PageLocal.ENTITY_NAME)
data class PageLocal(
    @PrimaryKey @ColumnInfo(name = ORDINAL) val ordinal: Int,
    @ColumnInfo(name = TOTAL_PAGES) val totalPages: Int,
    @ColumnInfo(name = TOTAL_RESULTS) val totalResults: Int
) {

    companion object {
        const val ENTITY_NAME = "page_local"
        const val ORDINAL = "ordinal"
        const val TOTAL_PAGES = "total_pages"
        const val TOTAL_RESULTS = "total_results"
    }
}