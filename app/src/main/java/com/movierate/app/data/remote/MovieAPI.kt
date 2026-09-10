package com.movierate.app.data.remote

import com.movierate.app.data.model.Movie
import retrofit2.http.GET

interface MovieApi {
    @GET("movies")
    suspend fun getMovies(): MovieResponse
}