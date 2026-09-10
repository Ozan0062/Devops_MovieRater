package com.movierate.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.movierate.app.data.model.EnabledRatings
import com.movierate.app.data.model.Movie
import com.movierate.app.data.repository.MovieRepository
import com.movierate.app.ui.components.FilterState
import com.movierate.app.ui.components.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val searchQuery: String = "",
    val movies: List<Movie> = emptyList(),
    val allMovies: List<Movie> = emptyList(),
    val filterState: FilterState = FilterState(),
    val sortOption: SortOption = SortOption.MOST_POPULAR,
    val isSortDescending: Boolean = true,
    val enabledRatings: EnabledRatings = EnabledRatings()
)

class HomeViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    // UI-state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            // Hent data fra backend gennem repository
            val movies = movieRepository.fetchMovies()

            // Opdater UI state med de hentede film
            _uiState.update {
                it.copy(
                    allMovies = movies,
                    movies = movies
                )
            }

            // Opdater filtre/sortering baseret på den nye data
            // (Disse skal beregnes igen nu hvor vi har data)
            updateAvailableFilters(movies)
        }
    }

    // Hjælpefunktion til at udregne genrer og årstal når data kommer ind
    private var _availableGenres: List<String> = emptyList()
    val availableGenres: List<String> get() = _availableGenres

    private var _availableYears: List<Int> = emptyList()
    val availableYears: List<Int> get() = _availableYears

    private fun updateAvailableFilters(movies: List<Movie>) {
        _availableGenres = movies.flatMap { it.genre }.distinct().sorted()
        _availableYears = movies.map { it.releaseYear }.distinct().sorted()
        // Her kan du evt. trigger et UI update, hvis dine filtre er i UI state
    }

    // --- EVENTS ---

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFiltersAndSort()
    }

    fun onFilterChange(filterState: FilterState) {
        _uiState.update { it.copy(filterState = filterState) }
        applyFiltersAndSort()
    }

    fun onSortChange(option: SortOption) {
        _uiState.update { state ->
            if (state.sortOption == option) {
                // trykket på samme -> toggle retning
                state.copy(isSortDescending = !state.isSortDescending)
            } else {
                // ny sortering -> vælg fornuftig default-retning
                val defaultDesc = when (option) {
                    SortOption.IMDB,
                    SortOption.ROTTEN_TOMATOES,
                    SortOption.METACRITIC,
                    SortOption.NEWEST_FIRST -> true       // højest først / nyest først
                    SortOption.OLDEST_FIRST -> false      // ældst først
                    SortOption.TITLE_A_Z -> false         // A–Z
                    SortOption.TITLE_Z_A -> true          // Z–A
                    SortOption.MOST_POPULAR -> true
                }
                state.copy(
                    sortOption = option,
                    isSortDescending = defaultDesc
                )
            }
        }
        applyFiltersAndSort()
    }

    fun onEnabledRatingsChange(enabledRatings: EnabledRatings) {
        _uiState.update { it.copy(enabledRatings = enabledRatings) }
        // påvirker kun visning, ikke filtrering
    }

    // --- FILTRERING + SORTERING ---

    private fun applyFiltersAndSort() {
        val state = _uiState.value
        var list = state.allMovies

        // Søgning i titel + instruktør
        val q = state.searchQuery.trim()
        if (q.isNotEmpty()) {
            list = list.filter { movie ->
                movie.title.contains(q, ignoreCase = true) ||
                        movie.director.contains(q, ignoreCase = true)
            }
        }

        // Genre-filter
        if (state.filterState.selectedGenres.isNotEmpty()) {
            list = list.filter { movie ->
                movie.genre.any { it in state.filterState.selectedGenres }
            }
        }

        // År-filter: "From selected year"
        if (state.filterState.selectedYears.isNotEmpty()) {
            val minYear = state.filterState.selectedYears.minOrNull()
            if (minYear != null) {
                list = list.filter { it.releaseYear >= minYear }
            }
        }

        // Minimum rating (IMDb)
        if (state.filterState.minRating > 0f) {
            list = list.filter { it.ratings.imdb >= state.filterState.minRating.toDouble() }
        }

        // Sortering
        list = when (state.sortOption) {
            SortOption.MOST_POPULAR ->
                list

            SortOption.IMDB ->
                if (state.isSortDescending)
                    list.sortedByDescending { it.ratings.imdb }
                else
                    list.sortedBy { it.ratings.imdb }

            SortOption.ROTTEN_TOMATOES ->
                if (state.isSortDescending)
                    list.sortedByDescending { it.ratings.rottenTomatoes }
                else
                    list.sortedBy { it.ratings.rottenTomatoes }

            SortOption.METACRITIC ->
                if (state.isSortDescending)
                    list.sortedByDescending { it.ratings.metacritic }
                else
                    list.sortedBy { it.ratings.metacritic }

            SortOption.NEWEST_FIRST ->
                list.sortedByDescending { it.releaseYear }

            SortOption.OLDEST_FIRST ->
                list.sortedBy { it.releaseYear }

            SortOption.TITLE_A_Z ->
                list.sortedBy { it.title }

            SortOption.TITLE_Z_A ->
                list.sortedByDescending { it.title }
        }
        _uiState.update { it.copy(movies = list) }
    }
}

// Factory til at kunne give MovieRepository ind i ViewModel
class HomeViewModelFactory(
    private val movieRepository: MovieRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(movieRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}