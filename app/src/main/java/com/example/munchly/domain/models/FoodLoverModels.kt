package com.example.munchly.domain.models

// ============================================================================
// DOMAIN MODELS - Pure business entities (infrastructure-agnostic)
// ============================================================================

data class BookmarkDomain(
    val id: String,
    val userId: String,
    val restaurantId: String,
    val createdAt: Long
)

data class AchievementDomain(
    val id: String,
    val userId: String,
    val achievementType: AchievementTypeDomain,
    val earnedAt: Long,
    val progress: Int,
    val requirement: Int
) {
    val isCompleted: Boolean
        get() = progress >= requirement

    val completionPercentage: Int
        get() = if (requirement > 0) {
            ((progress.toFloat() / requirement) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
}

/**
 * UPDATED: Achievement requirements and descriptions
 * - Rating Expert: 25 → 15 (only ratings)
 * - Bookmark Collector: 15 → 10 (unique restaurants)
 * - Veggie Lover: 10 → 5 (unique vegan restaurants, ratings only)
 * - Foodie Explorer: 20 unique restaurants (reviews + ratings)
 */
enum class AchievementTypeDomain(
    val title: String,
    val description: String,
    val icon: String,
    val requirement: Int
) {
    CULINARY_CRITIC(
        title = "Culinary Critic",
        description = "Leave 10 detailed reviews",
        icon = "🍴",
        requirement = 10
    ),
    RATING_EXPERT(
        title = "Rating Expert",
        description = "Rate 15 restaurants",
        icon = "⭐",
        requirement = 15  // CHANGED: 25 → 15, counts ratings only
    ),
    BOOKMARK_COLLECTOR(
        title = "Bookmark Collector",
        description = "Bookmark 10 different restaurants",
        icon = "📖",
        requirement = 10  // CHANGED: 15 → 10, counts unique restaurants
    ),
    VEGGIE_LOVER(
        title = "Veggie Lover",
        description = "Rate 5 different vegan restaurants",
        icon = "🥗",
        requirement = 5  // CHANGED: 10 → 5, counts unique vegan restaurants with ratings
    ),
    FOODIE_EXPLORER(
        title = "Foodie Explorer",
        description = "Visit 20 different restaurants",
        icon = "🗺️",
        requirement = 20  // Counts unique restaurants (reviews OR ratings)
    )
}

/**
 * UPDATED: User statistics now track UNIQUE restaurants
 */
data class UserStatsDomain(
    val userId: String,
    val totalReviews: Int,  // Reviews with comments
    val totalRatings: Int,  // Ratings with rating > 0
    val totalBookmarks: Int,  // DEPRECATED - use uniqueBookmarkedRestaurants instead
    val veganRestaurantsRated: Int,  // DEPRECATED - use uniqueVeganRestaurantsRated instead
    val uniqueRestaurantsVisited: Int,  // Count of unique restaurants (derived from Set)
    val lastUpdated: Long,

    // NEW: Track unique restaurant sets
    val uniqueBookmarkedRestaurants: Set<String> = emptySet(),  // Set of restaurant IDs
    val uniqueVeganRestaurantsRated: Set<String> = emptySet(),  // Set of vegan restaurant IDs with ratings
    val uniqueRestaurantsWithRatings: Set<String> = emptySet(),  // Set of restaurant IDs with ratings
    val uniqueVisitedRestaurants: Set<String> = emptySet()  // Set of restaurant IDs (reviews OR ratings) - for Foodie Explorer
)

// ============================================================================
// INPUT MODELS - For creating/updating domain entities
// ============================================================================

data class ReviewInput(
    val restaurantId: String,
    val userId: String,
    val userName: String,
    val rating: Double,
    val comment: String
) {
    val isRatingOnly: Boolean
        get() = comment.trim().isEmpty() && rating > 0.0

    val isReviewOnly: Boolean
        get() = comment.trim().isNotEmpty() && rating == 0.0
}

data class RestaurantListItem(
    val id: String,
    val name: String,
    val tags: List<String>,
    val priceRange: PriceRangeDomain,
    val averageRating: Double,
    val totalReviews: Int,
    val images: List<String>
)