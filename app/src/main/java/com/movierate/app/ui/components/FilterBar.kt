package com.movierate.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton   // 👈 VIGTIG IMPORT
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movierate.app.ui.theme.MutedForeground
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward


// --- MODELS ---

data class FilterState(
    val selectedGenres: Set<String> = emptySet(),
    val selectedYears: Set<Int> = emptySet(),  // vi bruger ét år via slider
    val minRating: Float = 0f                  // 0 = All, ellers 0–10
) {
    val isActive: Boolean
        get() = selectedGenres.isNotEmpty() ||
                selectedYears.isNotEmpty() ||
                minRating > 0f
}

// --- PUBLIC COMPOSABLE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    availableGenres: List<String>,
    availableYears: List<Int>,
    filterState: FilterState,
    onFilterChange: (FilterState) -> Unit,
    sortOption: SortOption,
    isSortDescending: Boolean,
    onSortChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFilterSheetOpen by remember { mutableStateOf(false) }
    var isSortSheetOpen by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // FILTER
        OutlinedButton(
            onClick = { isFilterSheetOpen = true },
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.weight(1f)
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter"
            )
            Spacer(Modifier.width(4.dp))
            Text("Filter")
            Spacer(Modifier.width(2.dp))
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        val showArrow = sortOption == SortOption.IMDB ||
                sortOption == SortOption.ROTTEN_TOMATOES ||
                sortOption == SortOption.METACRITIC

        // SORT – knap der åbner SortBottomSheet
        ElevatedButton(
            onClick = { isSortSheetOpen = true },
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(sortLabel(sortOption))

            if (showArrow) {
                Spacer(Modifier.width(4.dp))
                androidx.compose.material3.Icon(
                    imageVector = if (isSortDescending)
                        Icons.Filled.ArrowDownward
                    else
                        Icons.Filled.ArrowUpward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)   // lille pil
                )
            }

            Spacer(Modifier.width(2.dp))

            androidx.compose.material3.Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        }

        // CLEAR – kun hvis noget er aktivt
        if (filterState.isActive) {
            TextButton(
                onClick = { onFilterChange(FilterState()) },
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear filters"
                )
                Spacer(Modifier.width(4.dp))
                Text("Clear")
            }
        }
    }

    // FILTER SHEET
    if (isFilterSheetOpen) {
        FilterBottomSheet(
            availableGenres = availableGenres,
            availableYears = availableYears,
            filterState = filterState,
            onFilterChange = onFilterChange,
            onDismiss = { isFilterSheetOpen = false }
        )
    }

    // SORT SHEET – bruger den separate SortBottomSheet-fil
    if (isSortSheetOpen) {
        SortBottomSheet(
            current = sortOption,
            isDescending = isSortDescending,
            onSelected = { option ->
                onSortChange(option)
                isSortSheetOpen = false
            },
            onDismiss = { isSortSheetOpen = false }
        )
    }
}

// --- FILTER BOTTOM SHEET ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterBottomSheet(
    availableGenres: List<String>,
    availableYears: List<Int>,
    filterState: FilterState,
    onFilterChange: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val minYear = availableYears.minOrNull() ?: 1900
    val maxYear = availableYears.maxOrNull() ?: minYear
    val selectedYear = filterState.selectedYears.firstOrNull() ?: minYear

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = spacedBy(16.dp)
        ) {
            // Header
            Column(verticalArrangement = spacedBy(4.dp)) {
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Refine the movies you see",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedForeground
                )
            }

            // GENRE
            Text(
                text = "Genre",
                style = MaterialTheme.typography.labelLarge,
                color = MutedForeground
            )
            FlowRow(
                horizontalArrangement = spacedBy(8.dp),
                verticalArrangement = spacedBy(8.dp)
            ) {
                availableGenres.forEach { genre ->
                    val selected = genre in filterState.selectedGenres
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val newSet =
                                if (selected) filterState.selectedGenres - genre
                                else filterState.selectedGenres + genre
                            onFilterChange(filterState.copy(selectedGenres = newSet))
                        },
                        label = { Text(genre) }
                    )
                }
            }

            // YEAR SLIDER (samme stil som Minimum Rating)
            Column(verticalArrangement = spacedBy(8.dp)) {
                val yearLabel =
                    if (selectedYear == minYear) "Year: Any"
                    else "Year: From $selectedYear"

                Text(
                    text = yearLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MutedForeground
                )

                Slider(
                    value = selectedYear.toFloat(),
                    onValueChange = { value ->
                        val year = value.toInt().coerceIn(minYear, maxYear)
                        onFilterChange(filterState.copy(selectedYears = setOf(year)))
                    },
                    valueRange = minYear.toFloat()..maxYear.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(minYear.toString(), color = MutedForeground)
                    Text(maxYear.toString(), color = MutedForeground)
                }
            }

            // MINIMUM RATING SLIDER
            Column(verticalArrangement = spacedBy(6.dp)) {
                val ratingLabel = if (filterState.minRating <= 0f) {
                    "Minimum Rating: All"
                } else {
                    "Minimum Rating: ${String.format("%.1f", filterState.minRating)}+"
                }

                Text(
                    text = ratingLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MutedForeground
                )

                Slider(
                    value = filterState.minRating,
                    onValueChange = { value ->
                        val clamped = value.coerceIn(0f, 10f)
                        onFilterChange(filterState.copy(minRating = clamped))
                    },
                    valueRange = 0f..10f,
                    steps = 19,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedForeground
                    )
                    Text(
                        text = "10",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedForeground
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}