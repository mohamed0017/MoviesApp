package com.personal.moviesapp.domain.usecase

import com.personal.moviesapp.data.model.MovieResponse
import com.personal.moviesapp.di.IoDispatcher
import com.personal.moviesapp.domain.model.Result
import com.personal.moviesapp.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<MovieResponse> = withContext(dispatcher) {
        try {
            if (query.isBlank()) {
                return@withContext Result.Error(IllegalArgumentException("Search query cannot be empty"))
            }
            Result.Success(repository.searchMovies(query, page))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
} 