package com.kmkole86.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageResponse(
    @SerialName(value = "page")
    val ordinal: Int,
    @SerialName(value = "results")
    val movies: List<MovieResponse>,
    @SerialName(value = "total_pages")
    val totalPages: Int,
    @SerialName(value = "total_results")
    val totalResults: Int
)