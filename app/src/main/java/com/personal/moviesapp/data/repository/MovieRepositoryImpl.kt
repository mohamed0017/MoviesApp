package com.personal.moviesapp.data.repository

import com.personal.moviesapp.data.model.MovieResponse
import com.personal.moviesapp.data.remote.MovieApiService
import com.personal.moviesapp.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApiService
) : MovieRepository {
    override suspend fun getPopularMovies(page: Int): MovieResponse {
        return api.getPopularMovies(page= page)
    }

    override suspend fun searchMovies(query: String, page: Int): MovieResponse {
        return api.searchMovies(query, page)
    }
} 