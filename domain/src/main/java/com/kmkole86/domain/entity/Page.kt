package com.kmkole86.domain.entity

data class Page(
    val ordinal: Int,
    val totalPages: Int,
    val totalResults: Int,
    val movies: List<Movie>
)

val Page.hasNext: Boolean
    get() = ordinal != totalPages