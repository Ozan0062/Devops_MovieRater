package com.movierate.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.movierate.app.data.model.EnabledRatings
import com.movierate.app.data.model.Movie
import com.movierate.app.ui.components.logos.ImdbLogo
import com.movierate.app.ui.components.logos.MetacriticLogo
import com.movierate.app.ui.components.logos.RottenTomatoesLogo
import com.movierate.app.ui.theme.MutedForeground
import com.movierate.app.ui.theme.Primary
import com.movierate.app.utils.getRatingColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movie: Movie,
    enabledRatings: EnabledRatings,
    onBack: () -> Unit
) {
    var isFullscreenOpen by remember { mutableStateOf(false) }
    val imdbRating = String.format("%.1f", movie.ratings.imdb)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Poster - Clickable for fullscreen
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { isFullscreenOpen = true },
                contentScale = ContentScale.Crop
            )

            // Movie Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Meta info
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = movie.releaseYear.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text("•", color = MutedForeground)
                        Text(
                            text = "${movie.duration} min",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedForeground
                        )
                        Text("•", color = MutedForeground)
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = movie.rating,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Director with styled name
                    Text(
                        text = buildAnnotatedString {
                            append("Directed by ")
                            withStyle(style = SpanStyle(color = Primary)) {
                                append(movie.director)
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedForeground
                    )

                    // Views
                    Text(
                        text = "${movie.views} views",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedForeground
                    )
                }
            }

            // Ratings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Ratings",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // IMDb
                        if (enabledRatings.imdb) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ImdbLogo(size = 64.dp)
                                Text(
                                    text = imdbRating,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = getRatingColor(movie.ratings.imdb, "imdb")
                                )
                                Text(
                                    text = "out of 10",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MutedForeground
                                )
                            }
                        }

                        // Rotten Tomatoes
                        if (enabledRatings.rottenTomatoes) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RottenTomatoesLogo(size = 64.dp)
                                Text(
                                    text = "${movie.ratings.rottenTomatoes}%",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = getRatingColor(
                                        movie.ratings.rottenTomatoes.toDouble(),
                                        "rt"
                                    )
                                )
                                Text(
                                    text = "Tomatometer",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MutedForeground
                                )
                            }
                        }

                        // Metacritic
                        if (enabledRatings.metacritic) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetacriticLogo(size = 64.dp)
                                Text(
                                    text = movie.ratings.metacritic.toString(),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = getRatingColor(
                                        movie.ratings.metacritic.toDouble(),
                                        "metacritic"
                                    )
                                )
                                Text(
                                    text = "Metascore",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MutedForeground
                                )
                            }
                        }
                    }
                }
            }

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = movie.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedForeground
                    )
                }
            }

            // Additional Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Genre
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Genre",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedForeground
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            movie.genre.forEach { genre ->
                                Surface(
                                    color = Primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = genre,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        ),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Primary
                                    )
                                }
                            }
                        }
                    }

                    // Language
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Language",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedForeground
                        )
                        Text(
                            text = movie.language,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Cast
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Cast",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedForeground
                        )
                        Text(
                            text = movie.cast.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Country
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Country",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedForeground
                        )
                        Text(
                            text = movie.country,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Image Dialog
    if (isFullscreenOpen) {
        Dialog(
            onDismissRequest = { isFullscreenOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable { isFullscreenOpen = false }
            ) {
                // Close Button
                IconButton(
                    onClick = { isFullscreenOpen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Fullscreen Image
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = "${'$'}{movie.title} Poster",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
