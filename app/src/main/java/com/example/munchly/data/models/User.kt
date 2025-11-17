package com.example.munchly.data.models

enum class UserType {
    FOOD_LOVER,
    RESTAURANT_OWNER
}

data class User(
    val uid: String,
    val email: String,
    val userType: UserType,
    val username: String? = null,
    val name: String? = null
)

data class RegistrationData(
    val email: String,
    val password: String,
    val username: String,
    val userType: UserType
)