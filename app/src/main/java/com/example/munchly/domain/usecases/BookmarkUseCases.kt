package com.example.munchly.domain.usecases

import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.BookmarkDomain
import com.example.munchly.domain.repositories.BookmarkRepository

// ============================================================================
// BOOKMARK USE CASES - Application-specific bookmark operations
// ============================================================================

/**
 * Toggles bookmark status for a restaurant.
 * If bookmarked, removes it. If not bookmarked, adds it.
 */
class ToggleBookmarkUseCase(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(
        userId: String,
        restaurantId: String
    ): Result<Boolean> {
        if (userId.isBlank() || restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("User ID and Restaurant ID are required")
            )
        }

        // Check if already bookmarked
        val existingBookmark = repository.getBookmark(userId, restaurantId)
            .getOrNull()

        return if (existingBookmark != null) {
            // Remove bookmark
            repository.deleteBookmark(existingBookmark.id)
                .map { false } // Returns false = removed
        } else {
            // Add bookmark
            val newBookmark = BookmarkDomain(
                id = "",
                userId = userId,
                restaurantId = restaurantId,
                createdAt = System.currentTimeMillis()
            )
            repository.createBookmark(newBookmark)
                .map { true } // Returns true = added
        }
    }
}

/**
 * Retrieves all bookmarked restaurants for a user.
 */
class GetUserBookmarksUseCase(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(userId: String): Result<List<BookmarkDomain>> {
        if (userId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("User ID is required")
            )
        }

        return repository.getUserBookmarks(userId)
    }
}

/**
 * Checks if a restaurant is bookmarked by a user.
 */
class IsRestaurantBookmarkedUseCase(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(
        userId: String,
        restaurantId: String
    ): Result<Boolean> {
        if (userId.isBlank() || restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("User ID and Restaurant ID are required")
            )
        }

        return repository.isRestaurantBookmarked(userId, restaurantId)
    }
}