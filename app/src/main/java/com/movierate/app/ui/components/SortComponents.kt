package com.movierate.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movierate.app.ui.theme.MutedForeground

// --- SORT MODEL ---

enum class SortOption {
    MOST_POPULAR,
    IMDB,
    ROTTEN_TOMATOES,
    METACRITIC,
    NEWEST_FIRST,
    OLDEST_FIRST,
    TITLE_A_Z,
    TITLE_Z_A
}

// --- SORT BOTTOM SHEET ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    current: SortOption,
    isDescending: Boolean,
    onSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        SortOption.MOST_POPULAR,
        SortOption.IMDB,
        SortOption.ROTTEN_TOMATOES,
        SortOption.METACRITIC,
        SortOption.NEWEST_FIRST,
        SortOption.OLDEST_FIRST,
        SortOption.TITLE_A_Z,
        SortOption.TITLE_Z_A
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Choose how to sort movies",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedForeground
            )

            Spacer(Modifier.height(8.dp))

            options.forEach { option ->
                val selected = option == current

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelected(option) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sortLabel(option),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.weight(1f))

                    // ⭐ Kun disse får pile ⭐
                    val shouldShowArrow = option == SortOption.IMDB ||
                            option == SortOption.ROTTEN_TOMATOES ||
                            option == SortOption.METACRITIC

                    if (selected && shouldShowArrow) {
                        androidx.compose.material3.Icon(
                            imageVector = if (isDescending)
                                Icons.Default.ArrowDownward
                            else
                                Icons.Default.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp) // lille pil
                        )
                    }
                }
            }
        }
    }
}

// --- HELPERS ---

fun sortLabel(option: SortOption): String = when (option) {
    SortOption.MOST_POPULAR    -> "Most Popular"
    SortOption.IMDB            -> "IMDb"
    SortOption.ROTTEN_TOMATOES -> "Rotten Tomatoes"
    SortOption.METACRITIC      -> "Metacritic"
    SortOption.NEWEST_FIRST    -> "Newest First"
    SortOption.OLDEST_FIRST    -> "Oldest First"
    SortOption.TITLE_A_Z       -> "Title A–Z"
    SortOption.TITLE_Z_A       -> "Title Z–A"
}