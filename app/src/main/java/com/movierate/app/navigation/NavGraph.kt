package com.movierate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.movierate.app.data.model.EnabledRatings
import com.movierate.app.data.repository.MovieRepository
import com.movierate.app.ui.screens.HomeScreen
import com.movierate.app.ui.screens.MovieDetailsScreen
import com.movierate.app.ui.screens.SearchResultsScreen
import com.movierate.app.viewmodel.HomeViewModel
import com.movierate.app.viewmodel.HomeViewModelFactory
import com.movierate.app.viewmodel.MovieDetailsViewModel
import com.movierate.app.viewmodel.MovieDetailsViewModelFactory
import com.movierate.app.viewmodel.SearchViewModel
import com.movierate.app.viewmodel.SearchViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    movieRepository: MovieRepository,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // HOME SCREEN
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(movieRepository)
            )
            val uiState by homeViewModel.uiState.collectAsState()

            HomeScreen(
                uiState = uiState,
                onMovieClick = { movie ->
                    navController.navigate(Screen.MovieDetails.createRoute(movie.id))
                },
                onSearchQueryChange = homeViewModel::onSearchQueryChange,
                onFilterChange = homeViewModel::onFilterChange,
                onSortChange = homeViewModel::onSortChange,
                onRatingsChange = homeViewModel::onEnabledRatingsChange,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }

        // SEARCH RESULTS SCREEN
        composable(
            route = Screen.SearchResults.route,
            arguments = listOf(
                navArgument("query") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModelFactory(movieRepository, query)
            )
            val uiState by searchViewModel.uiState.collectAsState()

            SearchResultsScreen(
                uiState = uiState,
                enabledRatings = EnabledRatings(),   // faste kilder her
                onBack = { navController.navigateUp() },
                onMovieClick = { movie ->
                    navController.navigate(Screen.MovieDetails.createRoute(movie.id))
                },
                onSearchQueryChange = searchViewModel::onSearchQueryChange
            )
        }

        // MOVIE DETAILS SCREEN
        composable(
            route = Screen.MovieDetails.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val detailsViewModel: MovieDetailsViewModel = viewModel(
                factory = MovieDetailsViewModelFactory(movieRepository, movieId)
            )
            val movie by detailsViewModel.movie.collectAsState()

            movie?.let {
                MovieDetailsScreen(
                    movie = it,
                    enabledRatings = EnabledRatings(),   // også bare alle kilder her
                    onBack = { navController.navigateUp() }
                )
            }
        }
    }
}