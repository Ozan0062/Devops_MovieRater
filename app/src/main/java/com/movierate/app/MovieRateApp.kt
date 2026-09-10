package com.movierate.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.movierate.app.data.local.PreferencesManager
import com.movierate.app.data.repository.MovieRepository
import com.movierate.app.navigation.NavGraph
import com.movierate.app.ui.theme.MovieRateTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.movierate.app.data.remote.MovieApi
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun MovieRateApp(preferencesManager: PreferencesManager) {

    val isDarkTheme by preferencesManager.isDarkTheme.collectAsState(initial = true)
    val scope = rememberCoroutineScope()
    // Navigation controller
    val navController = rememberNavController()

    val movieRepository = remember {
        val retrofit = Retrofit.Builder()
            // HUSK: 10.0.2.2 for Emulator, din IP for fysisk telefon
            .baseUrl("http://192.168.1.33:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(MovieApi::class.java)
        MovieRepository(api)
    }

    MovieRateTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(
                navController = navController,
                movieRepository = movieRepository,
                isDarkTheme = isDarkTheme,
                onThemeChange = { enabled ->
                    scope.launch { preferencesManager.setDarkTheme(enabled) }
                }
            )
        }
    }
}