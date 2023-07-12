package com.kmkole86.data.model

data class MovieData(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val voteAverage: Float,
    val voteCount: Int
)