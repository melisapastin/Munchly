package com.example.munchly.data.repository

import com.example.munchly.data.models.UserProfile
import com.example.munchly.data.remote.BookmarkRemoteDataSource
import com.example.munchly.data.remote.ReviewRemoteDataSource
import com.example.munchly.data.remote.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val bookmarkRemoteDataSource: BookmarkRemoteDataSource,
    private val reviewRemoteDataSource: ReviewRemoteDataSource
) {
    // Get complete user profile with statistics
    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            // 1. Get basic user data
            val user = userRemoteDataSource.getUser(userId) ?: return null

            // 2. Get statistics efficiently
            val bookmarksCount = bookmarkRemoteDataSource.getBookmarksCount(userId)
            val reviewsCount = reviewRemoteDataSource.getReviewsCount(userId)

            UserProfile(
                uid = user.uid,
                email = user.email,
                username = user.username ?: "User",
                userType = user.userType,
                joinedDate = user.createdAt,
                bookmarksCount = bookmarksCount,
                reviewsCount = reviewsCount
            )
        } catch (e: Exception) {
            null
        }
    }

    // Stream user profile for real-time updates
    fun getUserProfileStream(userId: String): Flow<UserProfile?> = flow {
        // For now, emit once - can be enhanced with Firestore snapshots later
        emit(getUserProfile(userId))
    }
}