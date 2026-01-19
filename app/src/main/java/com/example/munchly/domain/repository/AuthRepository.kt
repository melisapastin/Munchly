package com.example.munchly.domain.repository

import com.example.munchly.domain.models.UserDomain
import com.example.munchly.domain.models.UserTypeDomain

// ============================================================================
// REPOSITORY INTERFACE - Domain layer contract for authentication
// ============================================================================

/**
 * Repository interface defining authentication operations.
 * This interface lives in the domain layer and is implemented by the data layer.
 * This achieves Dependency Inversion: domain depends on abstraction,
 * data layer depends on the same abstraction.
 *
 * Follows the same pattern as RestaurantRepository.
 */
interface AuthRepository {

    /**
     * Authenticates a user with email and password.
     * @return User domain model if successful
     */
    suspend fun login(email: String, password: String): Result<UserDomain>

    /**
     * Creates a new user account.
     * @return Created user domain model if successful
     */



    suspend fun signInWithGoogle(idToken: String): Result<UserDomain>



    suspend fun register(
        email: String,
        password: String,
        username: String,
        userType: UserTypeDomain
    ): Result<UserDomain>
}

