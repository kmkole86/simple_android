package com.kmkole86.data.model

data class PageData(
    val ordinal: Int,
    val totalPages: Int,
    val totalResults: Int,
    val movies: List<MovieData>
)