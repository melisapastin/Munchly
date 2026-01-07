package com.example.munchly.domain.usecases

import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.repositories.BookmarkRepository

/**
 * Counts the actual number of bookmarks for a restaurant.
 * This provides the real-time bookmark count instead of relying on cached stats.
 */
class CountRestaurantBookmarksUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    /**
     * Counts how many users have bookmarked this restaurant.
     *
     * @param restaurantId The ID of the restaurant to count bookmarks for
     * @return The count of bookmarks, or 0 if none exist
     */
    suspend operator fun invoke(restaurantId: String): Result<Int> {
        if (restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("Restaurant ID is required")
            )
        }

        // Get all bookmarks for this restaurant
        val bookmarksResult = bookmarkRepository.getRestaurantBookmarks(restaurantId)

        return if (bookmarksResult.isSuccess) {
            val bookmarks = bookmarksResult.getOrNull() ?: emptyList()
            Result.success(bookmarks.size)
        } else {
            bookmarksResult.map { 0 }
        }
    }
}