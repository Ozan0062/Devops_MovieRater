package com.movierate.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movierate.app.data.model.Movie
import com.movierate.app.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class SearchUiState(
    val query: String = "",
    val results: List<Movie> = emptyList()
)

class SearchViewModel(
    private val movieRepository: MovieRepository,
    initialQuery: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState(query = initialQuery))
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        performSearch(initialQuery)
    }

    private fun performSearch(query: String) {
        val results = movieRepository.searchMovies(query)
        _uiState.update { it.copy(results = results) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        performSearch(query)
    }
}

class SearchViewModelFactory(
    private val movieRepository: MovieRepository,
    private val initialQuery: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(movieRepository, initialQuery) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
