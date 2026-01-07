package com.example.munchly.data.repository

import com.example.munchly.data.mappers.toDomain
import com.example.munchly.data.remote.RestaurantSearchDataSource
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.repositories.RestaurantSearchRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

// ============================================================================
// REPOSITORY IMPLEMENTATION
// ============================================================================

/**
 * Implementation of RestaurantSearchRepository that uses Firebase as data source.
 * Handles exception mapping and data model conversion for restaurant discovery.
 */
class RestaurantSearchRepositoryImpl(
    private val remoteDataSource: RestaurantSearchDataSource
) : RestaurantSearchRepository {

    override suspend fun getAllRestaurants(): Result<List<RestaurantDomain>> =
        safeCall(resourceName = "Restaurants") {
            remoteDataSource
                .getAllRestaurants()
                .map { it.toDomain() }
        }

    override suspend fun searchRestaurantsByName(
        query: String
    ): Result<List<RestaurantDomain>> =
        safeCall(resourceName = "Restaurants") {
            remoteDataSource
                .searchRestaurantsByName(query)
                .map { it.toDomain() }
        }

    override suspend fun searchRestaurantsByTag(
        tag: String
    ): Result<List<RestaurantDomain>> =
        safeCall(resourceName = "Restaurants") {
            remoteDataSource
                .searchRestaurantsByTag(tag)
                .map { it.toDomain() }
        }

    override suspend fun getRestaurantById(
        restaurantId: String
    ): Result<RestaurantDomain?> =
        safeCall(resourceName = "Restaurant") {
            remoteDataSource
                .getRestaurantById(restaurantId)
                ?.toDomain()
        }

    // ========================================================================
    // EXCEPTION MAPPING
    // ========================================================================

    private suspend fun <T> safeCall(
        resourceName: String = "Resource",
        block: suspend () -> T
    ): Result<T> =
        try {
            Result.success(block())
        } catch (e: FirebaseNetworkException) {
            Result.failure(
                DomainException.NetworkError(
                    originalCause = e
                )
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
