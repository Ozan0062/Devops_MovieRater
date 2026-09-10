package com.movierate.app.data.repository

import com.movierate.app.data.model.Movie
import com.movierate.app.data.remote.MovieApi

class MovieRepository(private val api: MovieApi) {

    // Lokal cache, så vi kan søge/filtrere uden at hente data igen og igen
    private var cachedMovies: List<Movie> = emptyList()

    // Henter fra API og opdaterer cachen
    suspend fun fetchMovies(): List<Movie> {
        return try {
            val response = api.getMovies()
            val movies = response.content // Hent listen ud af wrapperen
            cachedMovies = movies
            movies
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Returnerer den senest hentede liste
    fun getAllMovies(): List<Movie> = cachedMovies

    fun getMovieById(id: Int): Movie? = cachedMovies.find { it.id == id }

    fun searchMovies(query: String): List<Movie> {
        if (query.isBlank()) return emptyList()
        return cachedMovies.filter { movie ->
            movie.title.contains(query, ignoreCase = true) ||
                    movie.director.contains(query, ignoreCase = true)
        }
    }
}