package com.example.munchly.data.remote

import com.example.munchly.data.models.Bookmark
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// ============================================================================
// FIRESTORE COLLECTION NAMES
// ============================================================================

internal object BookmarkCollections {
    const val BOOKMARKS = "bookmarks"
}

// ============================================================================
// REMOTE DATA SOURCE INTERFACE
// ============================================================================

/**
 * Interface defining remote data operations for bookmarks.
 * Abstracts Firebase-specific implementation details.
 */
interface BookmarkRemoteDataSource {

    suspend fun createBookmark(bookmark: Bookmark): Bookmark

    suspend fun deleteBookmark(bookmarkId: String)

    suspend fun getUserBookmarks(userId: String): List<Bookmark>

    suspend fun getBookmark(userId: String, restaurantId: String): Bookmark?

    suspend fun isRestaurantBookmarked(userId: String, restaurantId: String): Boolean

    /**
     * ADDED: Gets all bookmarks for a specific restaurant.
     */
    suspend fun getRestaurantBookmarks(restaurantId: String): List<Bookmark>
}

// ============================================================================
// FIREBASE IMPLEMENTATION
// ============================================================================

/**
 * Firebase Firestore implementation of BookmarkRemoteDataSource.
 * Handles all Firebase-specific logic and data transformations.
 */
class BookmarkRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : BookmarkRemoteDataSource {

    override suspend fun createBookmark(bookmark: Bookmark): Bookmark {
        val docRef = firestore
            .collection(BookmarkCollections.BOOKMARKS)
            .document()

        val bookmarkWithId = bookmark.copy(id = docRef.id)

        docRef.set(bookmarkWithId).await()

        return bookmarkWithId
    }

    override suspend fun deleteBookmark(bookmarkId: String) {
        firestore
            .collection(BookmarkCollections.BOOKMARKS)
            .document(bookmarkId)
            .delete()
            .await()
    }

    override suspend fun getUserBookmarks(userId: String): List<Bookmark> {
        // FIXED: Removed orderBy to avoid requiring Firestore index
        // We'll sort in memory instead
        val querySnapshot = firestore
            .collection(BookmarkCollections.BOOKMARKS)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val bookmarks = querySnapshot.documents.mapNotNull { document ->
            document.toObject(Bookmark::class.java)
        }

        // Sort in memory by createdAt (newest first)
        return bookmarks.sortedByDescending { it.createdAt }
    }

    override suspend fun getBookmark(
        userId: String,
        restaurantId: String
    ): Bookmark? {
        val querySnapshot = firestore
            .collection(BookmarkCollections.BOOKMARKS)
            .whereEqualTo("userId", userId)
            .whereEqualTo("restaurantId", restaurantId)
            .limit(1)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()
            ?.toObject(Bookmark::class.java)
    }

    override suspend fun isRestaurantBookmarked(
        userId: String,
        restaurantId: String
    ): Boolean {
        return getBookmark(userId, restaurantId) != null
    }

    /**
     * ADDED: Gets all bookmarks for a specific restaurant.
     * Used to count how many users have bookmarked this restaurant.
     */
    override suspend fun getRestaurantBookmarks(restaurantId: String): List<Bookmark> {
        val querySnapshot = firestore
            .collection(BookmarkCollections.BOOKMARKS)
            .whereEqualTo("restaurantId", restaurantId)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Bookmark::class.java)
        }
    }
}