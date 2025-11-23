package com.example.munchly.data.models

data class RegistrationData(
    val email: String,
    val password: String,
    val username: String,
    val userType: UserType
)