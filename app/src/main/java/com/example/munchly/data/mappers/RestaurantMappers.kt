package com.example.munchly.data.mappers

import com.example.munchly.data.models.DaySchedule
import com.example.munchly.data.models.PriceRange
import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.models.RestaurantStats
import com.example.munchly.data.models.Review
import com.example.munchly.domain.models.DayScheduleDomain
import com.example.munchly.domain.models.PriceRangeDomain
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.RestaurantStatsDomain
import com.example.munchly.domain.models.ReviewDomain

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
 * Converts domain restaurant to data DTO for Firebase persistence.
 */
fun RestaurantDomain.toData(): Restaurant {
    return Restaurant(
        id = id,
        ownerId = ownerId,
        name = name,
        description = description,
        tags = tags,
        priceRange = priceRange.toData(),
        address = address,
        phone = phone,
        openingHours = openingHours.toFirestoreFormat(),
        menuPdfUrl = menuPdfUrl,
        images = images,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Converts domain price range to data enum.
 */
fun PriceRangeDomain.toData(): PriceRange {
    return when (this) {
        PriceRangeDomain.BUDGET -> PriceRange.BUDGET
        PriceRangeDomain.MEDIUM -> PriceRange.MEDIUM
        PriceRangeDomain.EXPENSIVE -> PriceRange.EXPENSIVE
    }
}

/**
 * Converts domain day schedule to data model.
 */
fun DayScheduleDomain.toData(): DaySchedule {
    return DaySchedule(
        isOpen = isOpen,
        openTime = openTime,
        closeTime = closeTime
    )
}

/**
 * Converts domain opening hours map to Firestore format.
 * Firebase requires Map<String, Map<String, Any>> for nested objects.
 */
fun Map<String, DayScheduleDomain>.toFirestoreFormat(): Map<String, Map<String, Any>> {
    return this.mapValues { (_, schedule) ->
        schedule.toData().toMap()
    }
}

// ============================================================================
// DATA → DOMAIN (from persistence)
// ============================================================================

/**
 * Converts data DTO to domain restaurant model.
 */
fun Restaurant.toDomain(): RestaurantDomain {
    return RestaurantDomain(
        id = id,
        ownerId = ownerId,
        name = name,
        description = description,
        tags = tags,
        priceRange = priceRange.toDomain(),
        address = address,
        phone = phone,
        openingHours = openingHours.toDomainFormat(),
        menuPdfUrl = menuPdfUrl,
        images = images,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Converts data price range to domain enum.
 */
fun PriceRange.toDomain(): PriceRangeDomain {
    return when (this) {
        PriceRange.BUDGET -> PriceRangeDomain.BUDGET
        PriceRange.MEDIUM -> PriceRangeDomain.MEDIUM
        PriceRange.EXPENSIVE -> PriceRangeDomain.EXPENSIVE
    }
}

/**
 * Converts data day schedule to domain model.
 */
fun DaySchedule.toDomain(): DayScheduleDomain {
    return DayScheduleDomain(
        isOpen = isOpen,
        openTime = openTime,
        closeTime = closeTime
    )
}

/**
 * Converts Firestore opening hours format to domain map.
 */
fun Map<String, Map<String, Any>>.toDomainFormat(): Map<String, DayScheduleDomain> {
    return this.mapValues { (_, map) ->
        DaySchedule.fromMap(map).toDomain()
    }
}

/**
 * Converts stats DTO to domain model.
 */
fun RestaurantStats.toDomain(): RestaurantStatsDomain {
    return RestaurantStatsDomain(
        restaurantId = restaurantId,
        totalReviews = totalReviews,
        totalRatings = totalRatings,
        averageRating = averageRating,
        totalBookmarks = totalBookmarks,
        monthlyViews = monthlyViews,
        lastUpdated = lastUpdated
    )
}

/**
 * Converts review DTO to domain model.
 */
fun Review.toDomain(): ReviewDomain {
    return ReviewDomain(
        id = id,
        restaurantId = restaurantId,
        userId = userId,
        userName = userName,
        rating = rating,
        comment = comment,
        createdAt = createdAt
    )
}