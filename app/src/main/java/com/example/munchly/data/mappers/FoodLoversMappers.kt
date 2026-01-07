package com.example.munchly.data.mappers

import com.example.munchly.data.models.Achievement
import com.example.munchly.data.models.AchievementType
import com.example.munchly.data.models.Bookmark
import com.example.munchly.data.models.UserStats
import com.example.munchly.domain.models.AchievementDomain
import com.example.munchly.domain.models.AchievementTypeDomain
import com.example.munchly.domain.models.BookmarkDomain
import com.example.munchly.domain.models.UserStatsDomain

// ============================================================================
// MAPPERS - Convert between data DTOs and domain models
// ============================================================================

// ============================================================================
// DOMAIN → DATA (for persistence)
// ============================================================================

fun BookmarkDomain.toData(): Bookmark {
    return Bookmark(
        id = id,
        userId = userId,
        restaurantId = restaurantId,
        createdAt = createdAt
    )
}

fun AchievementDomain.toData(): Achievement {
    return Achievement(
        id = id,
        userId = userId,
        achievementType = achievementType.toData(),
        earnedAt = earnedAt,
        progress = progress,
        requirement = requirement
    )
}

fun AchievementTypeDomain.toData(): AchievementType {
    return when (this) {
        AchievementTypeDomain.CULINARY_CRITIC -> AchievementType.CULINARY_CRITIC
        AchievementTypeDomain.RATING_EXPERT -> AchievementType.RATING_EXPERT
        AchievementTypeDomain.BOOKMARK_COLLECTOR -> AchievementType.BOOKMARK_COLLECTOR
        AchievementTypeDomain.VEGGIE_LOVER -> AchievementType.VEGGIE_LOVER
        AchievementTypeDomain.FOODIE_EXPLORER -> AchievementType.FOODIE_EXPLORER
    }
}

/**
 * UPDATED: Maps domain stats to data stats (Set → List for Firestore)
 */
fun UserStatsDomain.toData(): UserStats {
    return UserStats(
        userId = userId,
        totalReviews = totalReviews,
        totalRatings = totalRatings,
        totalBookmarks = totalBookmarks,
        veganRestaurantsRated = veganRestaurantsRated,
        uniqueRestaurantsVisited = uniqueRestaurantsVisited,
        lastUpdated = lastUpdated,
        uniqueBookmarkedRestaurants = uniqueBookmarkedRestaurants.toList(),
        uniqueVeganRestaurantsRated = uniqueVeganRestaurantsRated.toList(),
        uniqueRestaurantsWithRatings = uniqueRestaurantsWithRatings.toList()
    )
}

// ============================================================================
// DATA → DOMAIN (from persistence)
// ============================================================================

fun Bookmark.toDomain(): BookmarkDomain {
    return BookmarkDomain(
        id = id,
        userId = userId,
        restaurantId = restaurantId,
        createdAt = createdAt
    )
}

fun Achievement.toDomain(): AchievementDomain {
    return AchievementDomain(
        id = id,
        userId = userId,
        achievementType = achievementType.toDomain(),
        earnedAt = earnedAt,
        progress = progress,
        requirement = requirement
    )
}

fun AchievementType.toDomain(): AchievementTypeDomain {
    return when (this) {
        AchievementType.CULINARY_CRITIC -> AchievementTypeDomain.CULINARY_CRITIC
        AchievementType.RATING_EXPERT -> AchievementTypeDomain.RATING_EXPERT
        AchievementType.BOOKMARK_COLLECTOR -> AchievementTypeDomain.BOOKMARK_COLLECTOR
        AchievementType.VEGGIE_LOVER -> AchievementTypeDomain.VEGGIE_LOVER
        AchievementType.FOODIE_EXPLORER -> AchievementTypeDomain.FOODIE_EXPLORER
    }
}

/**
 * UPDATED: Maps data stats to domain stats (List → Set)
 */
fun UserStats.toDomain(): UserStatsDomain {
    return UserStatsDomain(
        userId = userId,
        totalReviews = totalReviews,
        totalRatings = totalRatings,
        totalBookmarks = totalBookmarks,
        veganRestaurantsRated = veganRestaurantsRated,
        uniqueRestaurantsVisited = uniqueRestaurantsVisited,
        lastUpdated = lastUpdated,
        uniqueBookmarkedRestaurants = uniqueBookmarkedRestaurants.toSet(),
        uniqueVeganRestaurantsRated = uniqueVeganRestaurantsRated.toSet(),
        uniqueRestaurantsWithRatings = uniqueRestaurantsWithRatings.toSet()
    )
}