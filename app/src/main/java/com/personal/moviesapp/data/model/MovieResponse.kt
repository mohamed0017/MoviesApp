package com.personal.moviesapp.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class MovieResponse(
    val page: Int?,
    val results: List<MovieResponseItem>?,
    val totalPages: Int?,
    val totalResults: Int?
)

@Serializable
data class MovieResponseItem(
    val id: Int? = null,
    val title: String? = "",
    val overview: String? = "",
    @SerialName("poster_path")
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    @SerializedName("release_date")
    val releaseDate: String? = "",
    @SerialName("vote_average")
    @SerializedName("vote_average")
    val voteAverage: Double? = 0.0
)