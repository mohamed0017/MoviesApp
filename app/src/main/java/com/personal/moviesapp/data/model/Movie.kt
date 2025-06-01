package com.personal.moviesapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Int,
    val title: String = "",
    val overview: String = "",
    val posterPath: String? = null,
    val releaseDate: String = "",
    val voteAverage: Double = 0.0
) 