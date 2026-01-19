package com.example.munchly.data.models

// ============================================================================
// DATA MODELS - Pure DTOs for Firebase serialization
// ============================================================================

/**
 * Restaurant data transfer object for Firestore.
 * Contains only serialization logic, no business rules.
 */
data class Restaurant(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val priceRange: PriceRange = PriceRange.MEDIUM,
    val address: String = "",
    val phone: String = "",
    val openingHours: Map<String, Map<String, Any>> = emptyMap(), // Firestore format
    val menuPdfUrl: String = "",
    val images: List<String> = emptyList(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0,

)

/**
 * Day schedule data model for Firestore serialization.
 */
data class DaySchedule(
    val isOpen: Boolean = false,
    val openTime: String = "09:00",
    val closeTime: String = "22:00"
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "isOpen" to isOpen,
            "openTime" to openTime,
            "closeTime" to closeTime
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): DaySchedule {
            return DaySchedule(
                isOpen = map["isOpen"] as? Boolean ?: false,
                openTime = map["openTime"] as? String ?: "09:00",
                closeTime = map["closeTime"] as? String ?: "22:00"
            )
        }
    }
}

/**
 * Restaurant statistics DTO for Firestore.
 */
data class RestaurantStats(
    val restaurantId: String = "",
    val totalReviews: Int = 0,
    val totalRatings: Int = 0,
    val averageRating: Double = 0.0,
    val totalBookmarks: Int = 0,
    val monthlyViews: Int = 0,
    val lastUpdated: Long = 0
)

/**
 * Review DTO for Firestore.
 */
data class Review(
    val id: String = "",
    val restaurantId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val createdAt: Long = 0
)

/**
 * Price range enum for Firebase serialization.
 * Only contains data needed for persistence.
 */
enum class PriceRange {
    BUDGET,
    MEDIUM,
    EXPENSIVE
}