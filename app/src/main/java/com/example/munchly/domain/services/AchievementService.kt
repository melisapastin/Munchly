package com.example.munchly.domain.services

import com.example.munchly.domain.models.AchievementDomain
import com.example.munchly.domain.models.AchievementTypeDomain
import com.example.munchly.domain.models.UserStatsDomain

/**
 * FIXED: Achievement logic based on new requirements:
 * - Culinary Critic: ALL reviews with text (not unique) - 10 total
 * - Rating Expert: ALL ratings with stars (not unique) - 15 total
 * - Bookmark Collector: Current unique bookmarked restaurants - 10 unique
 * - Veggie Lover: Unique vegan restaurants rated - 5 unique
 * - Foodie Explorer: Unique restaurants visited - 20 unique
 */
class AchievementService {

    /**
     * Checks which new achievements should be awarded based on current stats.
     */
    fun checkNewAchievements(
        currentStats: UserStatsDomain,
        existingAchievements: List<AchievementDomain>
    ): List<AchievementTypeDomain> {
        val earnedTypes = existingAchievements.map { it.achievementType }.toSet()
        val newAchievements = mutableListOf<AchievementTypeDomain>()

        AchievementTypeDomain.entries.forEach { type ->
            if (type !in earnedTypes && shouldAwardAchievement(type, currentStats)) {
                newAchievements.add(type)
            }
        }

        return newAchievements
    }

    /**
     * FIXED: Uses correct counts for each achievement type
     */
    private fun shouldAwardAchievement(
        type: AchievementTypeDomain,
        stats: UserStatsDomain
    ): Boolean {
        return when (type) {
            AchievementTypeDomain.CULINARY_CRITIC ->
                // ALL reviews with text (not unique)
                stats.totalReviews >= type.requirement

            AchievementTypeDomain.RATING_EXPERT ->
                // ALL ratings with stars (not unique)
                stats.totalRatings >= type.requirement

            AchievementTypeDomain.BOOKMARK_COLLECTOR ->
                // Current unique bookmarked restaurants
                stats.uniqueBookmarkedRestaurants.size >= type.requirement

            AchievementTypeDomain.VEGGIE_LOVER ->
                // Unique vegan restaurants rated
                stats.uniqueVeganRestaurantsRated.size >= type.requirement

            AchievementTypeDomain.FOODIE_EXPLORER ->
                // Unique restaurants visited (review OR rating)
                stats.uniqueRestaurantsVisitedSet.size >= type.requirement
        }
    }

    /**
     * FIXED: Progress now uses correct counts
     */
    fun getAchievementProgress(
        type: AchievementTypeDomain,
        stats: UserStatsDomain
    ): Pair<Int, Int> {
        val progress = when (type) {
            AchievementTypeDomain.CULINARY_CRITIC ->
                // ALL reviews with text
                stats.totalReviews

            AchievementTypeDomain.RATING_EXPERT ->
                // ALL ratings with stars
                stats.totalRatings

            AchievementTypeDomain.BOOKMARK_COLLECTOR ->
                // Current unique bookmarks
                stats.uniqueBookmarkedRestaurants.size

            AchievementTypeDomain.VEGGIE_LOVER ->
                // Unique vegan restaurants rated
                stats.uniqueVeganRestaurantsRated.size

            AchievementTypeDomain.FOODIE_EXPLORER ->
                // Unique restaurants visited
                stats.uniqueRestaurantsVisitedSet.size
        }

        return Pair(progress, type.requirement)
    }

    fun createAchievement(
        userId: String,
        type: AchievementTypeDomain,
        stats: UserStatsDomain
    ): AchievementDomain {
        val (progress, requirement) = getAchievementProgress(type, stats)

        return AchievementDomain(
            id = "",
            userId = userId,
            achievementType = type,
            earnedAt = System.currentTimeMillis(),
            progress = progress,
            requirement = requirement
        )
    }
}