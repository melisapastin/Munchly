package com.example.munchly.data.models

import java.util.Date

data class Reply(
    val id: String,              // Unique reply identifier
    val userId: String,          // Author of this reply
    val userType: UserType,
    val comment: String,
    val likes: Int = 0,

    // Metadata
    val repliedAt: Date = Date(),    // When reply was posted
    val isOwnerReply: Boolean = false  // Special badge for restaurant owner
)