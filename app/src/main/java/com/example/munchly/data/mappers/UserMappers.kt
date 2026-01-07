package com.example.munchly.data.mappers

import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.domain.models.UserDomain
import com.example.munchly.domain.models.UserTypeDomain

// ============================================================================
// MAPPERS - Convert between data DTOs and domain models
// ============================================================================
// These mappers live in the data layer because they handle infrastructure
// concerns (Firebase serialization format) that domain shouldn't know about.
// ============================================================================

// ============================================================================
// DOMAIN → DATA (for persistence)
// ============================================================================

/**
 * Converts domain user to Firebase DTO.
 */
fun UserDomain.toData(): User {
    return User(
        uid = uid,
        email = email,
        userType = userType.toData(),
        username = username,
        name = name,
        createdAt = createdAt
    )
}

/**
 * Converts domain user type to data enum.
 */
fun UserTypeDomain.toData(): UserType {
    return when (this) {
        UserTypeDomain.FOOD_LOVER -> UserType.FOOD_LOVER
        UserTypeDomain.RESTAURANT_OWNER -> UserType.RESTAURANT_OWNER
    }
}

// ============================================================================
// DATA → DOMAIN (from persistence)
// ============================================================================

/**
 * Converts data DTO to domain user model.
 */
fun User.toDomain(): UserDomain {
    return UserDomain(
        uid = uid,
        email = email,
        userType = userType.toDomain(),
        username = username,
        name = name,
        createdAt = createdAt
    )
}

/**
 * Converts data user type to domain enum.
 */
fun UserType.toDomain(): UserTypeDomain {
    return when (this) {
        UserType.FOOD_LOVER -> UserTypeDomain.FOOD_LOVER
        UserType.RESTAURANT_OWNER -> UserTypeDomain.RESTAURANT_OWNER
    }
}