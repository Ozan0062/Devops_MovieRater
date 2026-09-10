package com.movierate.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movierate.app.data.model.EnabledRatings
import com.movierate.app.data.model.Movie
import com.movierate.app.ui.components.FilterBar
import com.movierate.app.ui.components.Header
import com.movierate.app.ui.components.RatingCard
import com.movierate.app.ui.components.FilterState
import com.movierate.app.ui.components.SortOption
import com.movierate.app.ui.theme.MutedForeground
import com.movierate.app.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onMovieClick: (Movie) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (FilterState) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onRatingsChange: (EnabledRatings) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header med søgefelt + menu
        Header(
            searchValue = uiState.searchQuery,
            onSearchChange = onSearchQueryChange,
            enabledRatings = uiState.enabledRatings,
            onRatingsChange = onRatingsChange,
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sektionstitel som på web
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Trending Movies",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Compare ratings from IMDb, Rotten Tomatoes, and Metacritic",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedForeground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tilgængelige genrer/år baseret på alle film
        val genres = remember(uiState.allMovies) {
            uiState.allMovies.flatMap { it.genre }.distinct().sorted()
        }

        val years = remember(uiState.allMovies) {
            uiState.allMovies.map { it.releaseYear }.distinct().sorted()  // evt. .sortedDescending() hvis du vil
        }


        // Filter + Sort bar
        FilterBar(
            availableGenres = genres,
            availableYears = years,
            filterState = uiState.filterState,
            onFilterChange = onFilterChange,
            sortOption = uiState.sortOption,
            isSortDescending = uiState.isSortDescending,
            onSortChange = onSortChange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.movies.isEmpty()) {
            Text(
                text = "No movies found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.movies) { movie ->
                    RatingCard(
                        movie = movie,
                        enabledRatings = uiState.enabledRatings,
                        onClick = { onMovieClick(movie) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}