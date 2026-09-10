package com.movierate.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.movierate.app.data.model.EnabledRatings
import com.movierate.app.data.model.Movie
import com.movierate.app.ui.components.logos.ImdbLogo
import com.movierate.app.ui.components.logos.MetacriticLogo
import com.movierate.app.ui.components.logos.RottenTomatoesLogo
import com.movierate.app.ui.theme.MutedForeground
import com.movierate.app.utils.getRatingColor

@Composable
fun RatingCard(
    movie: Movie,
    enabledRatings: EnabledRatings,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imdbRating = String.format("%.1f", movie.ratings.imdb)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Poster
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(96.dp)
                    .height(144.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Info section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Year
                Text(
                    text = movie.releaseYear.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedForeground
                )

                // Director
                Text(
                    text = "Directed by ${movie.director}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedForeground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                // Ratings
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // IMDb
                    if (enabledRatings.imdb) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImdbLogo(size = 32.dp)
                            Text(
                                text = imdbRating,
                                style = MaterialTheme.typography.bodyMedium,
                                color = getRatingColor(
                                    movie.ratings.imdb,
                                    "imdb"
                                )
                            )
                        }
                    }

                    // Rotten Tomatoes
                    if (enabledRatings.rottenTomatoes) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RottenTomatoesLogo(size = 32.dp)
                            Text(
                                text = "${movie.ratings.rottenTomatoes}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = getRatingColor(
                                    movie.ratings.rottenTomatoes.toDouble(),
                                    "rt"
                                )
                            )
                        }
                    }

                    // Metacritic
                    if (enabledRatings.metacritic) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MetacriticLogo(size = 28.dp)
                            Text(
                                text = movie.ratings.metacritic.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = getRatingColor(
                                    movie.ratings.metacritic.toDouble(),
                                    "metacritic"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
