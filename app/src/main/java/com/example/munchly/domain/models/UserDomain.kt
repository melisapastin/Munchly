package com.example.munchly.domain.models

// ============================================================================
// DOMAIN MODELS - Pure business entities (infrastructure-agnostic)
// ============================================================================

/**
 * User business entity.
 * Represents a user in the problem domain, independent of any
 * infrastructure concerns (Firebase, database, etc.)
 */
data class UserDomain(
    val uid: String,
    val email: String,
    val userType: UserTypeDomain,
    val username: String,
    val name: String?,
    val createdAt: Long
)

/**
 * User type classification.
 * Domain-level enum representing different user roles.
 */
enum class UserTypeDomain {
    FOOD_LOVER,
    RESTAURANT_OWNER;

    // ========================================================================
    // UI DISPLAY HELPERS
    // ========================================================================
    // Extension-like methods for UI display purposes.
    // Kept in domain enum to avoid creating additional files.
    // In production, these should use string resources for localization.
    // ========================================================================

    /**
     * Gets user-friendly display name for this user type.
     * Used in UI components for displaying user type to end users.
     */
    fun getDisplayName(): String {
        return when (this) {
            FOOD_LOVER -> "Food Lover"
            RESTAURANT_OWNER -> "Restaurant Owner"
        }
    }

    /**
     * Gets description text for this user type.
     * Used in registration flow to explain each user type option.
     */
    fun getDescription(): String {
        return when (this) {
            FOOD_LOVER -> "Discover and bookmark amazing restaurants"
            RESTAURANT_OWNER -> "Showcase and manage your restaurant"
        }
    }
}

// ============================================================================
// INPUT MODELS - For creating/updating domain entities
// ============================================================================

/**
 * Input model for user login.
 * Separates user input from persisted domain entities.
 */
data class LoginInput(
    val email: String,
    val password: String
)

/**
 * Input model for user registration.
 * Contains all data needed to create a new user account.
 */
data class RegisterInput(
    val email: String,
    val password: String,
    val username: String,
    val userType: UserTypeDomain
)