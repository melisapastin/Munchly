package com.example.munchly.data.repository

import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.remote.BookmarkRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookmarkRepository @Inject constructor(
    private val bookmarkRemoteDataSource: BookmarkRemoteDataSource
) {

    // Get all bookmarked restaurants for user
    suspend fun getBookmarks(userId: String): List<Restaurant> {
        return bookmarkRemoteDataSource.getBookmarks(userId)
    }

    // Toggle bookmark status for a restaurant
    suspend fun toggleBookmark(userId: String, restaurantId: String): Boolean {
        val currentlyBookmarked = bookmarkRemoteDataSource.isBookmarked(userId, restaurantId)
        return bookmarkRemoteDataSource.toggleBookmark(
            userId,
            restaurantId,
            !currentlyBookmarked
        )
    }

    // Check if restaurant is bookmarked by user
    suspend fun isBookmarked(userId: String, restaurantId: String): Boolean {
        return bookmarkRemoteDataSource.isBookmarked(userId, restaurantId)
    }

    // Stream bookmarks for real-time updates
    fun getBookmarksStream(userId: String): Flow<List<Restaurant>> = flow {
        emit(getBookmarks(userId))
    }
}