package com.personal.moviesapp.domain.repository

import com.personal.moviesapp.data.model.MovieResponse

interface MovieRepository {
    suspend fun getPopularMovies(page: Int): MovieResponse
    suspend fun searchMovies(query: String, page: Int = 1): MovieResponse
}