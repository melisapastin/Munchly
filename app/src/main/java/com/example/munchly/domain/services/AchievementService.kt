package com.example.munchly.domain.services

import com.example.munchly.domain.models.AchievementDomain
import com.example.munchly.domain.models.AchievementTypeDomain
import com.example.munchly.domain.models.UserStatsDomain

/**
 * UPDATED: Achievement logic now uses unique restaurant tracking
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
     * UPDATED: Uses unique restaurant counts for achievements
     */
    private fun shouldAwardAchievement(
        type: AchievementTypeDomain,
        stats: UserStatsDomain
    ): Boolean {
        return when (type) {
            AchievementTypeDomain.CULINARY_CRITIC ->
                stats.totalReviews >= type.requirement

            AchievementTypeDomain.RATING_EXPERT ->
                // CHANGED: Now uses totalRatings (only ratings with rating > 0)
                stats.totalRatings >= type.requirement

            AchievementTypeDomain.BOOKMARK_COLLECTOR ->
                // CHANGED: Now uses unique bookmarked restaurants
                stats.uniqueBookmarkedRestaurants.size >= type.requirement

            AchievementTypeDomain.VEGGIE_LOVER ->
                // CHANGED: Now uses unique vegan restaurants rated
                stats.uniqueVeganRestaurantsRated.size >= type.requirement

            AchievementTypeDomain.FOODIE_EXPLORER ->
                // Already correct: unique restaurants visited
                stats.uniqueRestaurantsVisited >= type.requirement
        }
    }

    /**
     * UPDATED: Progress now uses unique counts
     */
    fun getAchievementProgress(
        type: AchievementTypeDomain,
        stats: UserStatsDomain
    ): Pair<Int, Int> {
        val progress = when (type) {
            AchievementTypeDomain.CULINARY_CRITIC ->
                stats.totalReviews

            AchievementTypeDomain.RATING_EXPERT ->
                stats.totalRatings  // Only ratings > 0

            AchievementTypeDomain.BOOKMARK_COLLECTOR ->
                stats.uniqueBookmarkedRestaurants.size  // Unique restaurants

            AchievementTypeDomain.VEGGIE_LOVER ->
                stats.uniqueVeganRestaurantsRated.size  // Unique vegan restaurants

            AchievementTypeDomain.FOODIE_EXPLORER ->
                stats.uniqueRestaurantsVisited  // Unique restaurants
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