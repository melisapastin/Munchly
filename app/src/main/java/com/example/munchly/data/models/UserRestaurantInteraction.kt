package com.example.munchly.data.models

// Separate model for user-specific data
data class UserRestaurantInteraction(
    val userId: String,
    val restaurantId: String,
    val isBookmarked: Boolean = false,
    val userRating: Int? = null,  // This user's rating
    val hasVisited: Boolean = false
)