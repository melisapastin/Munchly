package com.example.munchly.data.repository

import com.example.munchly.data.mappers.toData
import com.example.munchly.data.mappers.toDomain
import com.example.munchly.data.remote.AchievementRemoteDataSource
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.AchievementDomain
import com.example.munchly.domain.models.UserStatsDomain
import com.example.munchly.domain.repositories.AchievementRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

// ============================================================================
// REPOSITORY IMPLEMENTATION
// ============================================================================

/**
 * Implementation of AchievementRepository that uses Firebase as data source.
 * Handles exception mapping and data model conversion.
 */
class AchievementRepositoryImpl(
    private val remoteDataSource: AchievementRemoteDataSource
) : AchievementRepository {

    override suspend fun getUserAchievements(userId: String): Result<List<AchievementDomain>> =
        safeCall(resourceName = "Achievements") {
            remoteDataSource.getUserAchievements(userId).map { it.toDomain() }
        }

    override suspend fun createAchievement(achievement: AchievementDomain): Result<AchievementDomain> =
        safeCall(resourceName = "Achievement") {
            remoteDataSource.createAchievement(achievement.toData()).toDomain()
        }

    override suspend fun getUserStats(userId: String): Result<UserStatsDomain?> =
        safeCall(resourceName = "UserStats") {
            remoteDataSource.getUserStats(userId)?.toDomain()
        }

    override suspend fun updateUserStats(userStats: UserStatsDomain): Result<UserStatsDomain> =
        safeCall(resourceName = "UserStats") {
            remoteDataSource.updateUserStats(userStats.toData()).toDomain()
        }

    // ========================================================================
    // EXCEPTION MAPPING
    // ========================================================================

    private suspend fun <T> safeCall(
        resourceName: String = "Resource",
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: FirebaseNetworkException) {
            Result.failure(
                DomainException.NetworkError(originalCause = e)
            )
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    Result.failure(
                        DomainException.PermissionDenied(
                            resource = resourceName,
                            originalCause = e
                        )
                    )

                FirebaseFirestoreException.Code.NOT_FOUND ->
                    Result.failure(
                        DomainException.ResourceNotFound(
                            resource = resourceName,
                            originalCause = e
                        )
                    )

                else ->
                    Result.failure(
                        DomainException.OperationFailed(
                            operation = "Firestore operation on $resourceName",
                            originalCause = e
                        )
                    )
            }
        } catch (e: Exception) {
            Result.failure(
                DomainException.Unknown(
                    reason = "Unexpected error: ${e.message ?: "No details"}",
                    originalCause = e
                )
            )
        }
    }
}