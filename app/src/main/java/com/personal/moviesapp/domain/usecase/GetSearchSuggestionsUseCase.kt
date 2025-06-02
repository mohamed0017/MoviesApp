package com.personal.moviesapp.domain.usecase

import com.personal.moviesapp.data.model.MovieResponse
import com.personal.moviesapp.di.IoDispatcher
import com.personal.moviesapp.domain.model.Result
import com.personal.moviesapp.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSearchSuggestionsUseCase @Inject constructor(
    private val repository: MovieRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(query: String, limit: Int = 5): Result<List<String>> = withContext(dispatcher) {
        try {
            if (query.length < 2) {
                return@withContext Result.Success(emptyList())
            }

            val movieResponse = repository.searchMovies(query, 1)
            val suggestions = movieResponse.results
                ?.take(limit)
                ?.mapNotNull { it.title }
                ?.distinct()
                ?: emptyList()
            Result.Success(suggestions)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
} 