package com.movierate.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movierate.app.data.model.EnabledRatings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    searchValue: String,
    onSearchChange: (String) -> Unit,
    enabledRatings: EnabledRatings,
    onRatingsChange: (EnabledRatings) -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menu Button
            IconButton(
                onClick = { isMenuOpen = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchValue,
                onValueChange = onSearchChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Search for movies...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }

    // Menu Sheet
    if (isMenuOpen) {
        ModalBottomSheet(
            onDismissRequest = { isMenuOpen = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            MenuContent(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                enabledRatings = enabledRatings,
                onRatingsChange = onRatingsChange
            )
        }
    }
}

@Composable
private fun MenuContent(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    enabledRatings: EnabledRatings,
    onRatingsChange: (EnabledRatings) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Settings Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Divider()

        // Theme Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isDarkTheme) "Dark mode" else "Light mode",
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeChange
            )
        }

        // Rating Sources
        Text(
            text = "Rating sources",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // IMDb
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = enabledRatings.imdb,
                    onCheckedChange = { checked ->
                        val enabledCount = listOf(
                            checked,
                            enabledRatings.rottenTomatoes,
                            enabledRatings.metacritic
                        ).count { it }
                        if (enabledCount > 0) {
                            onRatingsChange(enabledRatings.copy(imdb = checked))
                        }
                    }
                )
                Text("IMDb", style = MaterialTheme.typography.bodyMedium)
            }

            // Rotten Tomatoes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = enabledRatings.rottenTomatoes,
                    onCheckedChange = { checked ->
                        val enabledCount = listOf(
                            enabledRatings.imdb,
                            checked,
                            enabledRatings.metacritic
                        ).count { it }
                        if (enabledCount > 0) {
                            onRatingsChange(enabledRatings.copy(rottenTomatoes = checked))
                        }
                    }
                )
                Text("Rotten Tomatoes", style = MaterialTheme.typography.bodyMedium)
            }

            // Metacritic
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = enabledRatings.metacritic,
                    onCheckedChange = { checked ->
                        val enabledCount = listOf(
                            enabledRatings.imdb,
                            enabledRatings.rottenTomatoes,
                            checked
                        ).count { it }
                        if (enabledCount > 0) {
                            onRatingsChange(enabledRatings.copy(metacritic = checked))
                        }
                    }
                )
                Text("Metacritic", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Divider()

        // About
        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "MovieRate helps you compare movie ratings from IMDb, Rotten Tomatoes, and Metacritic all in one place.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        // Version
        Text(
            text = "Version",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
