package com.example.munchly.data.repository

import com.example.munchly.data.mappers.toData
import com.example.munchly.data.mappers.toDomain
import com.example.munchly.data.remote.RestaurantRemoteDataSource
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.RestaurantStatsDomain
import com.example.munchly.domain.models.ReviewDomain
import com.example.munchly.domain.repositories.RestaurantRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

// ============================================================================
// REPOSITORY IMPLEMENTATION
// ============================================================================

/**
 * Implementation of RestaurantRepository that uses Firebase as data source.
 * Handles exception mapping and data model conversion.
 */
class RestaurantRepositoryImpl(
    private val remoteDataSource: RestaurantRemoteDataSource
) : RestaurantRepository {

    override suspend fun getRestaurantByOwnerId(
        ownerId: String
    ): Result<RestaurantDomain?> =
        safeCall(resourceName = "Restaurant") {
            remoteDataSource.getRestaurantByOwnerId(ownerId)?.toDomain()
        }

    override suspend fun getRestaurantStats(
        restaurantId: String
    ): Result<RestaurantStatsDomain?> =
        safeCall(resourceName = "RestaurantStats") {
            remoteDataSource.getRestaurantStats(restaurantId)?.toDomain()
        }

    override suspend fun getRecentReviews(
        restaurantId: String,
        limit: Int
    ): Result<List<ReviewDomain>> =
        safeCall(resourceName = "Reviews") {
            remoteDataSource.getRecentReviews(restaurantId, limit)
                .map { it.toDomain() }
        }

    override suspend fun updateRestaurant(
        restaurant: RestaurantDomain
    ): Result<RestaurantDomain> =
        safeCall(resourceName = "Restaurant") {
            remoteDataSource.updateRestaurant(restaurant.toData()).toDomain()
        }

    override suspend fun createRestaurant(
        restaurant: RestaurantDomain
    ): Result<RestaurantDomain> =
        safeCall(resourceName = "Restaurant") {
            remoteDataSource.createRestaurant(restaurant.toData()).toDomain()
        }

    override suspend fun incrementRestaurantStats(
        restaurantId: String,
        newRating: Double,
        hasComment: Boolean
    ): Result<Unit> =
        safeCall(resourceName = "RestaurantStats") {
            remoteDataSource.incrementRestaurantStats(restaurantId, newRating, hasComment)
        }

    override suspend fun incrementRestaurantViews(restaurantId: String): Result<Unit> =
        safeCall(resourceName = "RestaurantStats") {
            remoteDataSource.incrementRestaurantViews(restaurantId)
        }

    // ========================================================================
    // EXCEPTION MAPPING
    // ========================================================================

    /**
     * Wraps remote data source calls to catch infrastructure exceptions
     * and map them to domain-specific exceptions.
     *
     * This ensures the domain layer remains independent of Firebase.
     *
     * @param resourceName Name of the resource being accessed (for error messages)
     * @param block The remote operation to execute
     * @return Result wrapping the operation outcome
     */
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
        } catch (e: IllegalArgumentException) {
            Result.failure(
                DomainException.InvalidData(
                    reason = e.message ?: "Invalid data for $resourceName",
                    originalCause = e
                )
            )
        } catch (e: IllegalStateException) {
            Result.failure(
                DomainException.OperationFailed(
                    operation = "Operation on $resourceName",
                    originalCause = e
                )
            )
        } catch (e: Exception) {
            // Catch unexpected exceptions that might indicate bugs
            Result.failure(
                DomainException.Unknown(
                    reason = "Unexpected error (${e.javaClass.simpleName}): ${e.message ?: "No details"}",
                    originalCause = e
                )
            )
        }
    }
}