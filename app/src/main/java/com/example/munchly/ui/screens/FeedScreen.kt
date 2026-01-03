package com.example.munchly.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.viewmodels.OwnerFeedViewModel

/**
 * Main feed screen that routes to appropriate content based on user type.
 * Shows different feeds for food lovers vs restaurant owners.
 */
@Composable
fun FeedScreen(
    userType: UserType,
    userId: String,
    ownerFeedViewModel: OwnerFeedViewModel?,
    onNavigateToRatings: () -> Unit,
    onNavigateToReviews: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (userType) {
        UserType.FOOD_LOVER -> {
            FoodLoverFeedScreen(modifier = modifier)
        }
        UserType.RESTAURANT_OWNER -> {
            // Use safe call instead of requireNotNull to avoid crashes
            ownerFeedViewModel?.let { viewModel ->
                OwnerFeedScreen(
                    viewModel = viewModel,
                    onNavigateToRatings = onNavigateToRatings,
                    onNavigateToReviews = onNavigateToReviews,
                    modifier = modifier
                )
            } ?: ErrorFeedScreen(
                message = "Unable to load restaurant dashboard",
                modifier = modifier
            )
        }
    }
}

/**
 * Placeholder feed for food lovers.
 * TODO: Implement restaurant browsing and search.
 */
@Composable
private fun FoodLoverFeedScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "There are no restaurants yet",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

/**
 * Error state when ViewModel is not available.
 */
@Composable
private fun ErrorFeedScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Red
        )
    }
}
