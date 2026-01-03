package com.example.munchly.domain.exceptions

sealed class AuthException(message: String) : Exception(message) {
    // Authentication errors
    object InvalidCredentials : AuthException("Invalid email or password")
    object UserNotFound : AuthException("No account found with this email")

    // Registration errors
    object EmailAlreadyInUse : AuthException("This email is already registered")
    object UsernameAlreadyTaken : AuthException("This username is already taken")
    object WeakPassword : AuthException("Password is too weak")

    // Data errors
    object UserDataNotFound : AuthException("User data not found")
    object InvalidUserData : AuthException("Invalid user data")

    // Network errors
    object NetworkError : AuthException("Network error. Please check your connection")

    // Generic
    class Unknown(message: String) : AuthException(message)
}