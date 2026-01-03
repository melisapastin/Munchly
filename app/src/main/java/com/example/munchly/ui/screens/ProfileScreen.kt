package com.example.munchly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.viewmodels.OwnerProfileViewModel
import com.example.munchly.data.models.UserType

/**
 * Main profile screen that routes to appropriate content based on user type.
 * Shows different profiles for food lovers vs restaurant owners.
 */
@Composable
fun ProfileScreen(
    userType: UserType,
    username: String,
    userId: String,
    ownerProfileViewModel: OwnerProfileViewModel?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (userType) {
        UserType.FOOD_LOVER -> {
            FoodLoverProfileScreen(
                username = username,
                modifier = modifier
            )
        }
        UserType.RESTAURANT_OWNER -> {
            // Use safe call instead of requireNotNull
            ownerProfileViewModel?.let { viewModel ->
                OwnerProfileScreen(
                    viewModel = viewModel,
                    onLogout = onLogout,
                    modifier = modifier
                )
            } ?: ErrorProfileScreen(
                message = "Unable to load profile",
                modifier = modifier
            )
        }
    }
}

/**
 * Placeholder profile for food lovers.
 * TODO: Implement food lover profile features.
 */
@Composable
private fun FoodLoverProfileScreen(
    username: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B4513)
        )
        // TODO: Add logout button and profile features for food lovers
    }
}

/**
 * Error state when ViewModel is not available.
 */
@Composable
private fun ErrorProfileScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Red
        )
    }
}