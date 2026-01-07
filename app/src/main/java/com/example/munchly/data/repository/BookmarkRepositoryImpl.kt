package com.example.munchly.data.repository

import com.example.munchly.data.mappers.toData
import com.example.munchly.data.mappers.toDomain
import com.example.munchly.data.remote.BookmarkRemoteDataSource
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.BookmarkDomain
import com.example.munchly.domain.repositories.BookmarkRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

// ============================================================================
// REPOSITORY IMPLEMENTATION
// ============================================================================

/**
 * Implementation of BookmarkRepository that uses Firebase as data source.
 * Handles exception mapping and data model conversion.
 */
class BookmarkRepositoryImpl(
    private val remoteDataSource: BookmarkRemoteDataSource
) : BookmarkRepository {

    override suspend fun createBookmark(bookmark: BookmarkDomain): Result<BookmarkDomain> =
        safeCall(resourceName = "Bookmark") {
            remoteDataSource.createBookmark(bookmark.toData()).toDomain()
        }

    override suspend fun deleteBookmark(bookmarkId: String): Result<Unit> =
        safeCall(resourceName = "Bookmark") {
            remoteDataSource.deleteBookmark(bookmarkId)
        }

    override suspend fun getUserBookmarks(userId: String): Result<List<BookmarkDomain>> =
        safeCall(resourceName = "Bookmarks") {
            remoteDataSource.getUserBookmarks(userId).map { it.toDomain() }
        }

    override suspend fun getBookmark(
        userId: String,
        restaurantId: String
    ): Result<BookmarkDomain?> =
        safeCall(resourceName = "Bookmark") {
            remoteDataSource.getBookmark(userId, restaurantId)?.toDomain()
        }

    override suspend fun isRestaurantBookmarked(
        userId: String,
        restaurantId: String
    ): Result<Boolean> =
        safeCall(resourceName = "Bookmark") {
            remoteDataSource.isRestaurantBookmarked(userId, restaurantId)
        }

    /**
     * ADDED: Gets all bookmarks for a specific restaurant.
     */
    override suspend fun getRestaurantBookmarks(restaurantId: String): Result<List<BookmarkDomain>> =
        safeCall(resourceName = "Bookmarks") {
            remoteDataSource.getRestaurantBookmarks(restaurantId).map { it.toDomain() }
        }

    // ========================================================================
    // EXCEPTION MAPPING
    // ========================================================================

    /**
     * Wraps remote data source calls to catch infrastructure exceptions
     * and map them to domain-specific exceptions.
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