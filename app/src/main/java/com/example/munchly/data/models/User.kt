package com.example.munchly.data.models

import java.util.Date

data class User(
    val uid: String,
    val email: String,
    val userType: UserType,
    val username: String? = null,
    val name: String? = null,
    val createdAt: Date = Date()
)