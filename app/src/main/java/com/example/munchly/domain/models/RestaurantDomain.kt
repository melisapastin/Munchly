package com.example.munchly.domain.models

// ============================================================================
// DOMAIN MODELS - Pure business entities (infrastructure-agnostic)
// ============================================================================

/**
 * Restaurant business entity.
 * Represents a restaurant in the problem domain, independent of any
 * infrastructure concerns (Firebase, database, etc.)
 */
data class RestaurantDomain(
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String,
    val tags: List<String>,
    val priceRange: PriceRangeDomain,
    val address: String,
    val latitude: Double = 0.0,  // Add this
    val longitude: Double = 0.0,
    val phone: String,
    val openingHours: Map<String, DayScheduleDomain>,
    val menuPdfUrl: String,
    val images: List<String>,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Day schedule for restaurant opening hours.
 */
data class DayScheduleDomain(
    val isOpen: Boolean,
    val openTime: String,  // Format: "HH:mm" (24-hour)
    val closeTime: String  // Format: "HH:mm" (24-hour)
)

/**
 * Price range classification for restaurants.
 */
enum class PriceRangeDomain(
    val symbol: String,
    val description: String
) {
    BUDGET("$", "Budget-friendly"),
    MEDIUM("$$", "Moderate pricing"),
    EXPENSIVE("$$$", "Fine dining")
}

/**
 * Restaurant statistics aggregation.
 */
data class RestaurantStatsDomain(
    val restaurantId: String,
    val totalReviews: Int,
    val totalRatings: Int,
    val averageRating: Double,
    val totalBookmarks: Int,
    val monthlyViews: Int,
    val lastUpdated: Long
)

/**
 * Customer review for a restaurant.
 */
data class ReviewDomain(
    val id: String,
    val restaurantId: String,
    val userId: String,
    val userName: String,
    val rating: Double,
    val comment: String,
    val createdAt: Long
)

// ============================================================================
// INPUT MODELS - For creating/updating domain entities
// ============================================================================

/**
 * Input model for creating or updating restaurants.
 * Separates user input from persisted domain entities.
 */
data class RestaurantInput(
    val ownerId: String,
    val name: String,
    val description: String,
    val tags: List<String>,
    val priceRange: PriceRangeDomain,
    val address: String,
    val phone: String,
    val openingHours: Map<String, DayScheduleDomain>,
    val menuPdfUrl: String = "",
    val images: List<String> = emptyList()
)

// ============================================================================
// DAYS OF WEEK - Shared domain constant
// ============================================================================

enum class DayOfWeek(val displayName: String) {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday")
}