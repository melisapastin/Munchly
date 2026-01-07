package com.example.munchly.data.models

// ============================================================================
// FOOD LOVER DATA MODELS - Pure DTOs for Firebase serialization
// ============================================================================

data class Bookmark(
    val id: String = "",
    val userId: String = "",
    val restaurantId: String = "",
    val createdAt: Long = 0
)

data class Achievement(
    val id: String = "",
    val userId: String = "",
    val achievementType: AchievementType = AchievementType.CULINARY_CRITIC,
    val earnedAt: Long = 0,
    val progress: Int = 0,
    val requirement: Int = 0
)

enum class AchievementType {
    CULINARY_CRITIC,
    RATING_EXPERT,
    BOOKMARK_COLLECTOR,
    VEGGIE_LOVER,
    FOODIE_EXPLORER
}

/**
 * FIXED: User statistics with corrected field names
 */
data class UserStats(
    val userId: String = "",
    val totalReviews: Int = 0,  // ALL reviews with text
    val totalRatings: Int = 0,  // ALL ratings with stars
    val totalBookmarks: Int = 0,  // Deprecated
    val veganRestaurantsRated: Int = 0,  // Deprecated
    val uniqueRestaurantsVisited: Int = 0,  // Deprecated
    val lastUpdated: Long = 0,

    // Lists for Firestore (converted from Sets in domain)
    val uniqueBookmarkedRestaurants: List<String> = emptyList(),
    val uniqueVeganRestaurantsRated: List<String> = emptyList(),
    val uniqueRestaurantsVisitedSet: List<String> = emptyList()  // RENAMED to match domain
)