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
 * FIXED: Achievement requirements and tracking logic
 * - Culinary Critic: 10 reviews with text (not unique)
 * - Rating Expert: 15 ratings with stars (not unique)
 * - Bookmark Collector: 10 unique bookmarked restaurants (current count)
 * - Veggie Lover: 5 unique vegan restaurants rated
 * - Foodie Explorer: 20 unique restaurants visited (review OR rating)
 */
enum class AchievementTypeDomain(
    val title: String,
    val description: String,
    val icon: String,
    val requirement: Int
) {
    CULINARY_CRITIC(
        title = "Culinary Critic",
        description = "Leave 10 reviews with comments",
        icon = "🍴",
        requirement = 10
    ),
    RATING_EXPERT(
        title = "Rating Expert",
        description = "Rate 15 restaurants",
        icon = "⭐",
        requirement = 15
    ),
    BOOKMARK_COLLECTOR(
        title = "Bookmark Collector",
        description = "Bookmark 10 different restaurants",
        icon = "📖",
        requirement = 10
    ),
    VEGGIE_LOVER(
        title = "Veggie Lover",
        description = "Rate 5 different vegan restaurants",
        icon = "🥗",
        requirement = 5
    ),
    FOODIE_EXPLORER(
        title = "Foodie Explorer",
        description = "Visit 20 different restaurants",
        icon = "🗺️",
        requirement = 20
    )
}

/**
 * FIXED: User statistics for achievement tracking
 * - totalReviews: Count of ALL reviews with text (not unique)
 * - totalRatings: Count of ALL ratings with stars (not unique)
 * - uniqueBookmarkedRestaurants: Set of currently bookmarked restaurant IDs
 * - uniqueVeganRestaurantsRated: Set of unique vegan restaurant IDs that were rated
 * - uniqueRestaurantsVisited: Set of unique restaurant IDs visited (review OR rating)
 */
data class UserStatsDomain(
    val userId: String,
    val totalReviews: Int = 0,  // ALL reviews with text (not unique)
    val totalRatings: Int = 0,  // ALL ratings with stars (not unique)
    val totalBookmarks: Int = 0,  // DEPRECATED - use uniqueBookmarkedRestaurants.size
    val veganRestaurantsRated: Int = 0,  // DEPRECATED - use uniqueVeganRestaurantsRated.size
    val uniqueRestaurantsVisited: Int = 0,  // DEPRECATED - use uniqueRestaurantsVisited.size
    val lastUpdated: Long = 0,

    // Sets for tracking unique restaurants
    val uniqueBookmarkedRestaurants: Set<String> = emptySet(),  // Current bookmarks
    val uniqueVeganRestaurantsRated: Set<String> = emptySet(),  // Unique vegan restaurants rated
    val uniqueRestaurantsVisitedSet: Set<String> = emptySet()  // RENAMED: Unique restaurants visited
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