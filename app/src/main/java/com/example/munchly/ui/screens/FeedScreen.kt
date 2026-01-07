package com.example.munchly.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.munchly.domain.models.UserTypeDomain
import com.example.munchly.ui.viewmodels.FoodLoverFeedViewModel
import com.example.munchly.ui.viewmodels.OwnerFeedViewModel

/**
 * Main feed screen that routes to appropriate content based on user type.
 * Shows different feeds for food lovers vs restaurant owners.
 */
@Composable
fun FeedScreen(
    userType: UserTypeDomain,
    userId: String,
    username: String,
    ownerFeedViewModel: OwnerFeedViewModel?,
    foodLoverFeedViewModel: FoodLoverFeedViewModel?,
    onNavigateToRatings: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToRestaurantDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (userType) {
        UserTypeDomain.FOOD_LOVER -> {
            foodLoverFeedViewModel?.let { viewModel ->
                FoodLoverFeedScreen(
                    viewModel = viewModel,
                    onRestaurantClick = onNavigateToRestaurantDetails,
                    modifier = modifier
                )
            }
        }
        UserTypeDomain.RESTAURANT_OWNER -> {
            ownerFeedViewModel?.let { viewModel ->
                OwnerFeedScreen(
                    viewModel = viewModel,
                    onNavigateToRatings = onNavigateToRatings,
                    onNavigateToReviews = onNavigateToReviews,
                    modifier = modifier
                )
            }
        }
    }
}