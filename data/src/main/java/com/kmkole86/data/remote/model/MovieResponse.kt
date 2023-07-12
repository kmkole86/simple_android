package com.kmkole86.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    @SerialName(value = "id")
    val id: Int,
    @SerialName(value = "title")
    val title: String,
    @SerialName(value = "overview")
    val overview: String,
    @SerialName(value = "poster_path")
    val posterPath: String,
    @SerialName(value = "release_date")
    val releaseDate: String,
    @SerialName(value = "vote_average")
    val voteAverage: Float,
    @SerialName(value = "vote_count")
    val voteCount: Int
)