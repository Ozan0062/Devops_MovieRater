package com.movierate.app.ui.components.logos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movierate.app.R

@Composable
fun MetacriticLogo(
    size: Dp = 28.dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.logo_metacritic),
        contentDescription = "Metacritic",
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}
