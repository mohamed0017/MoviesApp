package com.personal.moviesapp.presentation.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.personal.moviesapp.data.model.MovieResponseItem
import com.personal.moviesapp.presentation.movies.views.MovieItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    onMovieClick: (MovieResponseItem) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    val gridState = rememberLazyGridState()
    var isSearchFocused by remember { mutableStateOf(false) }

    // Calculate if we're near the end of the list
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = (state as? MoviesScreenUiState.Success)?.movies?.size ?: 0
            val threshold = 4 // Load more when 4 items away from the end

            lastVisibleItem >= totalItems - threshold
        }
    }

    LaunchedEffect(viewModel.uiSideEffect) {
        viewModel.uiSideEffect.collectLatest {
            when (it) {
                is MoviesScreenSideEffect.NavigateToDetails -> onMovieClick(it.movie)
            }
        }
    }
    // Load more when we're near the end
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && state is MoviesScreenUiState.Success) {
            val successState = state as MoviesScreenUiState.Success
            if (successState.hasNextPage) {
                viewModel.onEvent(MovieListEvent.LoadNextPage)
            }
        }
    }

    Scaffold(
        topBar = {
            Text(
                text = "Movies",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search Bar with Suggestions
                Box(Modifier
                    .fillMaxWidth()
                    .zIndex(1f)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            isSearchFocused = it.isNotEmpty()
                            viewModel.onEvent(MovieListEvent.OnSearchQueryChange(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                            .onFocusChanged { focusState ->
                                isSearchFocused = focusState.isFocused
                                if (!focusState.isFocused) {
                                    viewModel.onEvent(MovieListEvent.OnSearchFocusChange(false))
                                }
                            },
                        placeholder = { Text("Search movies...") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.onEvent(MovieListEvent.OnSearchSubmit(searchQuery))
                                isSearchFocused = false
                            }
                        )
                    )

                    // Suggestions Dropdown
                    if (isSearchFocused && searchSuggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = 64.dp)
                                .zIndex(1f),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.LightGray)
                                    .heightIn(max = 200.dp)
                            ) {
                                items(searchSuggestions.size) { index ->
                                    val suggestion = searchSuggestions[index]
                                    Text(
                                        text = suggestion,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.onEvent(
                                                    MovieListEvent.OnSuggestionClick(
                                                        suggestion
                                                    )
                                                )
                                                isSearchFocused = false
                                            }
                                            .padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (suggestion != searchSuggestions.last()) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }

                when (state) {
                    is MoviesScreenUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is MoviesScreenUiState.Success -> {
                        val movies = (state as MoviesScreenUiState.Success).movies
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(movies) { movie ->
                                MovieItem(
                                    movie = movie,
                                    onClick = { viewModel.onEvent(MovieListEvent.OnMovieClick(movie)) }
                                )
                            }
                        }
                    }

                    is MoviesScreenUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (state as MoviesScreenUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
