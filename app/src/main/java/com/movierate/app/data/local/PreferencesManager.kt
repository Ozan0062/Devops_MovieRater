package com.movierate.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.movierate.app.data.model.EnabledRatings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val IMDB_ENABLED_KEY = booleanPreferencesKey("imdb_enabled")
        private val RT_ENABLED_KEY = booleanPreferencesKey("rt_enabled")
        private val MC_ENABLED_KEY = booleanPreferencesKey("mc_enabled")
    }

    // Dark theme
    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: true
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    // Enabled ratings
    val enabledRatings: Flow<EnabledRatings> = context.dataStore.data.map { preferences ->
        EnabledRatings(
            imdb = preferences[IMDB_ENABLED_KEY] ?: true,
            rottenTomatoes = preferences[RT_ENABLED_KEY] ?: true,
            metacritic = preferences[MC_ENABLED_KEY] ?: true
        )
    }

    suspend fun setEnabledRatings(ratings: EnabledRatings) {
        context.dataStore.edit { preferences ->
            preferences[IMDB_ENABLED_KEY] = ratings.imdb
            preferences[RT_ENABLED_KEY] = ratings.rottenTomatoes
            preferences[MC_ENABLED_KEY] = ratings.metacritic
        }
    }
}
