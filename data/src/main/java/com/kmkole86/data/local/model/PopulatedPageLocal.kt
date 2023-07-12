package com.kmkole86.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kmkole86.data.local.model.MovieLocal
import com.kmkole86.data.local.model.PageLocal

data class PopulatedPageLocal(
    @Embedded val page: PageLocal,
    @Relation(
        parentColumn = PageLocal.ORDINAL,
        entityColumn = MovieLocal.PAGE_ORDINAL
    )
    val movies: List<MovieLocal>
)