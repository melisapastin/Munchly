package com.example.munchly.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.munchly.domain.models.UserTypeDomain
import com.example.munchly.ui.viewmodels.OwnerProfileViewModel

/**
 * Main profile screen that routes to appropriate content based on user type.
 * Shows different profiles for food lovers vs restaurant owners.
 */
@Composable
fun ProfileScreen(
    userType: UserTypeDomain,
    username: String,
    userId: String,
    ownerProfileViewModel: OwnerProfileViewModel?,
    onLogout: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (userType) {
        UserTypeDomain.FOOD_LOVER -> {
            FoodLoverProfileScreen(
                username = username,
                displayName = username, // Could be different if you store display names
                onBookmarksClick = onNavigateToBookmarks,
                onAchievementsClick = onNavigateToAchievements,
                onLogout = onLogout,
                modifier = modifier
            )
        }
        UserTypeDomain.RESTAURANT_OWNER -> {
            ownerProfileViewModel?.let { viewModel ->
                OwnerProfileScreen(
                    viewModel = viewModel,
                    onLogout = onLogout,
                    modifier = modifier
                )
            }
        }
    }
}