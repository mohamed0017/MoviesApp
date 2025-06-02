package com.personal.moviesapp.presentation.navigation

import com.personal.moviesapp.data.model.MovieResponseItem
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    data object MovieList : Screen("movie_list")
    data object MovieDetails : Screen("movie_details/{movieJson}") {
        fun createRoute(movie: MovieResponseItem): String {
            val json = json.encodeToString(MovieResponseItem.serializer(), movie)
            val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
            return "movie_details/$encodedJson"
        }
    }
} 