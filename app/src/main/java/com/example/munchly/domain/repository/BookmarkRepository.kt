package com.example.munchly.domain.repositories

import com.example.munchly.domain.models.BookmarkDomain

// ============================================================================
// REPOSITORY INTERFACE - Domain layer contract for bookmark data access
// ============================================================================

/**
 * Repository interface defining data access operations for bookmarks.
 * This interface lives in the domain layer and is implemented by the data layer.
 */
interface BookmarkRepository {

    /**
     * Creates a new bookmark for a restaurant.
     * @return Created bookmark with generated ID
     */
    suspend fun createBookmark(bookmark: BookmarkDomain): Result<BookmarkDomain>

    /**
     * Deletes a bookmark by its ID.
     */
    suspend fun deleteBookmark(bookmarkId: String): Result<Unit>

    /**
     * Retrieves all bookmarks for a user.
     * @return List of bookmarks ordered by creation date (newest first)
     */
    suspend fun getUserBookmarks(userId: String): Result<List<BookmarkDomain>>

    /**
     * Retrieves a specific bookmark for a user and restaurant.
     * @return Bookmark if exists, null otherwise
     */
    suspend fun getBookmark(userId: String, restaurantId: String): Result<BookmarkDomain?>

    /**
     * Checks if a restaurant is bookmarked by a user.
     * @return True if bookmarked, false otherwise
     */
    suspend fun isRestaurantBookmarked(userId: String, restaurantId: String): Result<Boolean>

    /**
    * ADDED: Retrieves all bookmarks for a specific restaurant.
    * Used to count how many users have bookmarked this restaurant.
    * @return List of all bookmarks for this restaurant
    */
    suspend fun getRestaurantBookmarks(restaurantId: String): Result<List<BookmarkDomain>>
}