package com.personal.moviesapp.presentation.movies

import com.personal.moviesapp.data.model.MovieResponseItem

sealed class MoviesScreenUiState {
    object Loading : MoviesScreenUiState()
    data class Success(
        val movies: List<MovieResponseItem>,
        val hasNextPage: Boolean = false
    ) : MoviesScreenUiState()
    data class Error(val message: String) : MoviesScreenUiState()
}

sealed class MoviesScreenSideEffect{
    data class NavigateToDetails(val movie: MovieResponseItem) : MoviesScreenSideEffect()
}