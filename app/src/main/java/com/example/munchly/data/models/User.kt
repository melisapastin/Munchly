package com.example.munchly.data.models

data class User(
    val uid: String,
    val email: String,
    val userType: UserType, // No default - must be provided
    val username: String,
    val name: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore with safe defaults
    constructor() : this("", "", UserType.FOOD_LOVER, "", null, 0L)
}