package com.movierate.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    val imdb: Double,
    val rottenTomatoes: Int,
    val metacritic: Int
) : Parcelable

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val director: String,
    val duration: Int,
    val rating: String,
    val views: String,
    val summary: String,
    val genre: List<String>,
    val cast: List<String>,
    val language: String,
    val country: String,
    val ratings: Rating,
    val posterUrl: String
) : Parcelable

data class EnabledRatings(
    val imdb: Boolean = true,
    val rottenTomatoes: Boolean = true,
    val metacritic: Boolean = true
)
