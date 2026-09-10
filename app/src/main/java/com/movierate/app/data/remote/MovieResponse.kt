package com.movierate.app.data.remote

import com.movierate.app.data.model.Movie

data class MovieResponse(
    val content: List<Movie>
)