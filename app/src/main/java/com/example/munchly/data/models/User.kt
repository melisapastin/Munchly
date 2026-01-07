package com.example.munchly.data.models

// ============================================================================
// DATA MODELS - Pure DTOs for Firebase serialization
// ============================================================================

/**
 * User data transfer object for Firestore.
 * Contains only serialization logic, no business rules.
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val userType: UserType = UserType.FOOD_LOVER,
    val username: String = "",
    val name: String? = null,
    val createdAt: Long = 0
)

/**
 * User type enum for Firebase serialization.
 * Only contains data needed for persistence.
 */
enum class UserType {
    FOOD_LOVER,
    RESTAURANT_OWNER
}