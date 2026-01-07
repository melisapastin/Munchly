package com.example.munchly.domain.usecases

import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.AchievementDomain
import com.example.munchly.domain.models.AchievementTypeDomain
import com.example.munchly.domain.models.UserStatsDomain
import com.example.munchly.domain.repositories.AchievementRepository
import com.example.munchly.domain.services.AchievementService

// ============================================================================
// ACHIEVEMENT USE CASES - Application-specific achievement operations
// ============================================================================

/**
 * Retrieves all achievements for a user, including progress.
 * Returns both earned and unearned achievements for UI display.
 */
class GetUserAchievementsUseCase(
    private val repository: AchievementRepository,
    private val achievementService: AchievementService = AchievementService()
) {
    suspend operator fun invoke(userId: String): Result<AchievementsWithProgress> {
        if (userId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("User ID is required")
            )
        }

        val earnedResult = repository.getUserAchievements(userId)
        if (earnedResult.isFailure) {
            return Result.failure(earnedResult.exceptionOrNull()!!)
        }

        val earned = earnedResult.getOrNull() ?: emptyList()

        val statsResult = repository.getUserStats(userId)
        val stats = statsResult.getOrNull() ?: createDefaultStats(userId)

        val allAchievements = AchievementTypeDomain.entries.map { type ->
            val earnedAchievement = earned.find { it.achievementType == type }

            if (earnedAchievement != null) {
                earnedAchievement
            } else {
                achievementService.createAchievement(userId, type, stats)
            }
        }

        return Result.success(
            AchievementsWithProgress(
                earned = earned,
                all = allAchievements
            )
        )
    }

    private fun createDefaultStats(userId: String) = UserStatsDomain(
        userId = userId,
        totalReviews = 0,
        totalRatings = 0,
        totalBookmarks = 0,
        veganRestaurantsRated = 0,
        uniqueRestaurantsVisited = 0,
        lastUpdated = System.currentTimeMillis(),
        uniqueBookmarkedRestaurants = emptySet(),
        uniqueVeganRestaurantsRated = emptySet(),
        uniqueRestaurantsVisitedSet = emptySet()  // FIXED
    )
}

data class AchievementsWithProgress(
    val earned: List<AchievementDomain>,
    val all: List<AchievementDomain>
)

/**
 * Updates user statistics and checks for new achievements.
 */
class UpdateUserStatsUseCase(
    private val repository: AchievementRepository,
    private val achievementService: AchievementService = AchievementService()
) {
    suspend operator fun invoke(
        userId: String,
        updateAction: (UserStatsDomain) -> UserStatsDomain
    ): Result<List<AchievementTypeDomain>> {
        if (userId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("User ID is required")
            )
        }

        val currentStats = repository.getUserStats(userId)
            .getOrNull() ?: createDefaultStats(userId)

        val updatedStats = updateAction(currentStats).copy(
            lastUpdated = System.currentTimeMillis()
        )

        val saveResult = repository.updateUserStats(updatedStats)
        if (saveResult.isFailure) {
            return Result.failure(saveResult.exceptionOrNull()!!)
        }

        val existingAchievements = repository.getUserAchievements(userId)
            .getOrNull() ?: emptyList()

        val newAchievements = achievementService.checkNewAchievements(
            currentStats = updatedStats,
            existingAchievements = existingAchievements
        )

        newAchievements.forEach { type ->
            val achievement = achievementService.createAchievement(
                userId = userId,
                type = type,
                stats = updatedStats
            )
            repository.createAchievement(achievement)
        }

        return Result.success(newAchievements)
    }

    private fun createDefaultStats(userId: String) = UserStatsDomain(
        userId = userId,
        totalReviews = 0,
        totalRatings = 0,
        totalBookmarks = 0,
        veganRestaurantsRated = 0,
        uniqueRestaurantsVisited = 0,
        lastUpdated = System.currentTimeMillis(),
        uniqueBookmarkedRestaurants = emptySet(),
        uniqueVeganRestaurantsRated = emptySet(),
        uniqueRestaurantsVisitedSet = emptySet()  // FIXED
    )
}