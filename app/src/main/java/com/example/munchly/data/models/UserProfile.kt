package com.example.munchly.data.models

import java.util.Date

data class UserProfile(
    // Identification
    val uid: String,
    val email: String,
    val username: String,
    val userType: UserType,

    // Activity Statistics
    val bookmarksCount: Int = 0,
    val reviewsCount: Int = 0,
    val ratingsCount: Int = 0,

    // Extras
    // val displayName: String? = null,      // Actual name
    // val profilePictureUrl: String? = null,
    val bio: String? = null,
    val location: String? = null,

    // Metadata
    val joinedDate: Date,
    val lastActive: Date? = null
)