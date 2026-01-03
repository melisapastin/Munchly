package com.example.munchly.data.models

data class User(
    val uid: String = "",
    val email: String = "",
    val userType: UserType = UserType.FOOD_LOVER,
    val username: String = "",
    val name: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)