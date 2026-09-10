package com.movierate.app.utils

import androidx.compose.ui.graphics.Color
import com.movierate.app.ui.theme.Green
import com.movierate.app.ui.theme.Red
import com.movierate.app.ui.theme.Yellow

fun getRatingColor(value: Double, platform: String): Color {
    return when (platform) {
        "imdb" -> {
            when {
                value >= 7.0 -> Green
                value >= 5.0 -> Yellow
                else -> Red
            }
        }
        "rt" -> {
            when {
                value >= 60.0 -> Green
                value >= 40.0 -> Yellow
                else -> Red
            }
        }
        "metacritic" -> {
            when {
                value >= 61.0 -> Green
                value >= 40.0 -> Yellow
                else -> Red
            }
        }
        else -> Color.White
    }
}
