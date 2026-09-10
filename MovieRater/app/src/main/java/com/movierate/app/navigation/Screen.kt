package com.movierate.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SearchResults : Screen("search/{query}") {
        fun createRoute(query: String) = "search/$query"
    }
    object MovieDetails : Screen("movie/{movieId}") {
        fun createRoute(movieId: Int) = "movie/$movieId"
    }
}
