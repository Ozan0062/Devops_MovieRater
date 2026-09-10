package com.movierate.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movierate.app.data.model.Movie
import com.movierate.app.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MovieDetailsViewModel(
    private val movieRepository: MovieRepository,
    movieId: Int
) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    init {
        _movie.value = movieRepository.getMovieById(movieId)
    }
}

class MovieDetailsViewModelFactory(
    private val movieRepository: MovieRepository,
    private val movieId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
            return MovieDetailsViewModel(movieRepository, movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
