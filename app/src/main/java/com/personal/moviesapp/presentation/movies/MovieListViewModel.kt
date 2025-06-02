package com.personal.moviesapp.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.moviesapp.data.model.MovieResponseItem
import com.personal.moviesapp.domain.model.Result
import com.personal.moviesapp.domain.usecase.GetPopularMoviesUseCase
import com.personal.moviesapp.domain.usecase.GetSearchSuggestionsUseCase
import com.personal.moviesapp.domain.usecase.SearchMoviesUseCase
import com.personal.moviesapp.presentation.movies.MoviesScreenSideEffect.NavigateToDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MoviesScreenUiState>(MoviesScreenUiState.Loading)
    val state: StateFlow<MoviesScreenUiState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MoviesScreenSideEffect>()
    val uiSideEffect: SharedFlow<MoviesScreenSideEffect> = _sideEffect.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    private var currentPage = 1
    private var isLoading = false
    private var hasNextPage = true
    private var searchJob: Job? = null
    private var suggestionsJob: Job? = null

    init {
        loadMovies()
    }

    fun onEvent(event: MovieListEvent) {
        when (event) {
            is MovieListEvent.LoadMovies -> loadMovies()
            is MovieListEvent.LoadNextPage -> loadNextPage()
            is MovieListEvent.OnMovieClick -> handleMovieClick(event.movie)
            is MovieListEvent.OnSearchQueryChange -> {
                _searchQuery.value = event.query
                updateSearchSuggestions(event.query)

            }
            is MovieListEvent.OnSuggestionClick -> {
                _searchQuery.value = event.suggestion
                searchMovies(event.suggestion)
            }
            is MovieListEvent.OnSearchFocusChange -> {
                if (event.isFocused && _searchQuery.value.length >= 2) {
                    updateSearchSuggestions(_searchQuery.value)
                } else {
                    _searchSuggestions.value = emptyList()
                }
            }

            is MovieListEvent.OnSearchSubmit -> {
                _searchQuery.value = event.query
                updateSearchSuggestions(event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500) // Debounce search
                    if (event.query.isNotEmpty()) {
                        searchMovies(event.query)
                    } else {
                        loadMovies()
                    }
                }
            }
        }
    }

    private fun updateSearchSuggestions(query: String) {
        suggestionsJob?.cancel()
        suggestionsJob = viewModelScope.launch {
            delay(300) // Debounce suggestions
            if (query.length >= 2) {
                when (val result = getSearchSuggestionsUseCase(query)) {
                    is Result.Success -> {
                        _searchSuggestions.value = result.data
                    }
                    is Result.Error -> {
                        // Don't show error for suggestions, just clear them
                        _searchSuggestions.value = emptyList()
                    }
                    is Result.Loading -> {
                        // Keep previous suggestions while loading
                    }
                }
            } else {
                _searchSuggestions.value = emptyList()
            }
        }
    }

    private fun loadMovies() {
        if (isLoading) return
        
        viewModelScope.launch {
            isLoading = true
            _state.value = MoviesScreenUiState.Loading
            when (val result = getPopularMoviesUseCase(currentPage)) {
                is Result.Success -> {
                    val movies = result.data
                    _state.value = MoviesScreenUiState.Success(
                        movies = movies.results ?: emptyList(),
                        hasNextPage = !movies.results.isNullOrEmpty()
                    )
                    hasNextPage = !movies.results.isNullOrEmpty()
                }
                is Result.Error -> {
                    _state.value = MoviesScreenUiState.Error(result.exception.message ?: "An error occurred")
                }
                is Result.Loading -> {
                    _state.value = MoviesScreenUiState.Loading
                }
            }
            isLoading = false
        }
    }

    private fun searchMovies(query: String) {
        if (isLoading) return
        
        viewModelScope.launch {
            isLoading = true
            _state.value = MoviesScreenUiState.Loading
            when (val result = searchMoviesUseCase(query, currentPage)) {
                is Result.Success -> {
                    val movies = result.data
                    _state.value = MoviesScreenUiState.Success(
                        movies = movies.results ?: emptyList(),
                        hasNextPage = !movies.results.isNullOrEmpty()
                    )
                    hasNextPage = !movies.results.isNullOrEmpty()
                    currentPage = 1
                }
                is Result.Error -> {
                    _state.value = MoviesScreenUiState.Error(result.exception.message ?: "An error occurred")
                }
                is Result.Loading -> {
                    _state.value = MoviesScreenUiState.Loading
                }
            }
            isLoading = false
        }
    }

    private fun loadNextPage() {
        if (isLoading || !hasNextPage) return
        
        viewModelScope.launch {
            isLoading = true
            val nextPage = currentPage + 1
            val result = if (_searchQuery.value.isNotEmpty()) {
                searchMoviesUseCase(_searchQuery.value, nextPage)
            } else {
                getPopularMoviesUseCase(nextPage)
            }

            when (result) {
                is Result.Success -> {
                    val newMovies = result.data
                    if (!newMovies.results.isNullOrEmpty()) {
                        val currentMovies = (_state.value as? MoviesScreenUiState.Success)?.movies ?: emptyList()
                        _state.value = MoviesScreenUiState.Success(
                            movies = currentMovies + newMovies.results,
                            hasNextPage = true
                        )
                        currentPage = nextPage
                    } else {
                        hasNextPage = false
                    }
                }
                is Result.Error -> {
                    _state.value = MoviesScreenUiState.Error(result.exception.message ?: "An error occurred")
                }
                is Result.Loading -> {
                    _state.value = MoviesScreenUiState.Loading
                }
            }
            isLoading = false
        }
    }

    private fun handleMovieClick(movie: MovieResponseItem) {
       viewModelScope.launch {
           _sideEffect.emit(NavigateToDetails(movie))
       }
    }
} 