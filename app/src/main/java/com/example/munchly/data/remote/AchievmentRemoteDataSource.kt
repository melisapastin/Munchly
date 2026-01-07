package com.example.munchly.data.remote

import com.example.munchly.data.models.Achievement
import com.example.munchly.data.models.UserStats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// ============================================================================
// FIRESTORE COLLECTION NAMES
// ============================================================================

internal object AchievementCollections {
    const val ACHIEVEMENTS = "achievements"
    const val USER_STATS = "user_stats"
}

// ============================================================================
// REMOTE DATA SOURCE INTERFACE
// ============================================================================

/**
 * Interface defining remote data operations for achievements.
 * Abstracts Firebase-specific implementation details.
 */
interface AchievementRemoteDataSource {

    suspend fun getUserAchievements(userId: String): List<Achievement>

    suspend fun createAchievement(achievement: Achievement): Achievement

    suspend fun getUserStats(userId: String): UserStats?

    suspend fun updateUserStats(userStats: UserStats): UserStats
}

// ============================================================================
// FIREBASE IMPLEMENTATION
// ============================================================================

/**
 * Firebase Firestore implementation of AchievementRemoteDataSource.
 * Handles achievement tracking and user statistics.
 */
class AchievementRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : AchievementRemoteDataSource {

    override suspend fun getUserAchievements(userId: String): List<Achievement> {
        val querySnapshot = firestore
            .collection(AchievementCollections.ACHIEVEMENTS)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Achievement::class.java)
        }
    }

    override suspend fun createAchievement(achievement: Achievement): Achievement {
        val docRef = firestore
            .collection(AchievementCollections.ACHIEVEMENTS)
            .document()

        val achievementWithId = achievement.copy(id = docRef.id)

        docRef.set(achievementWithId).await()

        return achievementWithId
    }

    override suspend fun getUserStats(userId: String): UserStats? {
        val statsDoc = firestore
            .collection(AchievementCollections.USER_STATS)
            .document(userId)
            .get()
            .await()

        return statsDoc.toObject(UserStats::class.java)
    }

    override suspend fun updateUserStats(userStats: UserStats): UserStats {
        firestore
            .collection(AchievementCollections.USER_STATS)
            .document(userStats.userId)
            .set(userStats)
            .await()

        return userStats
    }
}