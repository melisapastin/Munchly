package com.example.munchly.domain.repositories

import com.example.munchly.domain.models.AchievementDomain
import com.example.munchly.domain.models.UserStatsDomain

// ============================================================================
// REPOSITORY INTERFACE - Domain layer contract for achievement data access
// ============================================================================

/**
 * Repository interface defining data access operations for achievements.
 * This interface lives in the domain layer and is implemented by the data layer.
 */
interface AchievementRepository {

    /**
     * Retrieves all achievements for a user.
     * @return List of earned achievements
     */
    suspend fun getUserAchievements(userId: String): Result<List<AchievementDomain>>

    /**
     * Creates a new achievement for a user.
     * @return Created achievement with generated ID
     */
    suspend fun createAchievement(achievement: AchievementDomain): Result<AchievementDomain>

    /**
     * Retrieves user statistics for achievement tracking.
     * @return User stats if exist, null otherwise
     */
    suspend fun getUserStats(userId: String): Result<UserStatsDomain?>

    /**
     * Updates user statistics.
     * @return Updated user stats
     */
    suspend fun updateUserStats(userStats: UserStatsDomain): Result<UserStatsDomain>
}