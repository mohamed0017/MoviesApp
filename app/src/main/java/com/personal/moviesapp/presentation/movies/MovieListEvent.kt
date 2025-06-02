package com.personal.moviesapp.presentation.movies

import com.personal.moviesapp.data.model.MovieResponseItem

sealed class MovieListEvent {
    data object LoadMovies : MovieListEvent()
    data object LoadNextPage : MovieListEvent()
    data class OnMovieClick(val movie: MovieResponseItem) : MovieListEvent()
    data class OnSearchQueryChange(val query: String) : MovieListEvent()
} 