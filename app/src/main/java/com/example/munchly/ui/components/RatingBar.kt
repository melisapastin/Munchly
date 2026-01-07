package com.example.munchly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Interactive rating bar for selecting star ratings.
 * Displays 5 stars that can be clicked to set rating.
 */
@Composable
fun RatingBar(
    rating: Double,
    onRatingChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isFilled = i <= rating.toInt()

            Icon(
                imageVector = if (isFilled) {
                    Icons.Filled.Star
                } else {
                    Icons.Outlined.StarOutline
                },
                contentDescription = "$i star",
                tint = if (isFilled) {
                    Color(0xFFFFA726)
                } else {
                    Color(0xFFE0E0E0)
                },
                modifier = Modifier
                    .size(32.dp)
                    .clickable(enabled = enabled) {
                        onRatingChange(i.toDouble())
                    }
            )
        }
    }
}

/**
 * Read-only rating display for showing existing ratings.
 */
@Composable
fun RatingDisplay(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Int = 16
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isFilled = i <= rating.toInt()

            Icon(
                imageVector = if (isFilled) {
                    Icons.Filled.Star
                } else {
                    Icons.Outlined.StarOutline
                },
                contentDescription = null,
                tint = if (isFilled) {
                    Color(0xFFFFA726)
                } else {
                    Color(0xFFE0E0E0)
                },
                modifier = Modifier.size(starSize.dp)
            )
        }
    }
}