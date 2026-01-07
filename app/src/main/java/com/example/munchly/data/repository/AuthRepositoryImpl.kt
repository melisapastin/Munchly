package com.example.munchly.data.repository

import com.example.munchly.data.mappers.toData
import com.example.munchly.data.mappers.toDomain
import com.example.munchly.data.remote.AuthRemoteDataSource
import com.example.munchly.domain.exceptions.*
import com.example.munchly.domain.models.UserDomain
import com.example.munchly.domain.models.UserTypeDomain
import com.example.munchly.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

// ============================================================================
// REPOSITORY IMPLEMENTATION
// ============================================================================

/**
 * Implementation of AuthRepository that uses Firebase as data source.
 *
 * Responsibilities:
 * - Maps domain models to data models (and vice versa)
 * - Maps infrastructure exceptions to domain exceptions
 * - Delegates actual data operations to remote data source
 *
 * This layer ensures the domain layer remains independent of Firebase
 * and can easily swap out the implementation without affecting business logic.
 */
class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<UserDomain> = safeFirebaseCall("Login") {
        remoteDataSource.login(email, password).toDomain()
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String,
        userType: UserTypeDomain
    ): Result<UserDomain> = safeFirebaseCall("Registration") {
        remoteDataSource.register(
            email = email,
            password = password,
            username = username,
            userType = userType.toData()
        ).toDomain()
    }

    // ========================================================================
    // EXCEPTION MAPPING
    // ========================================================================

    /**
     * Wraps remote data source calls to catch infrastructure exceptions
     * and map them to domain-specific exceptions.
     *
     * This ensures the domain layer remains independent of Firebase and
     * only sees domain-level errors. Makes the repository implementation
     * reusable across all repository classes.
     *
     * @param operation Name of the operation (for logging/debugging)
     * @param block The remote operation to execute
     * @return Result wrapping either success value or domain exception
     */
    private suspend fun <T> safeFirebaseCall(
        operation: String,
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: FirebaseNetworkException) {
            // Network connectivity issues
            Result.failure(DomainException.NetworkError(originalCause = e))
        } catch (e: DataUsernameConflictException) {
            // Username already exists in database
            Result.failure(UsernameCollisionException(originalCause = e))
        } catch (e: DataIntegrityException) {
            // Corrupted or invalid data from remote source
            Result.failure(InvalidUserDataException(details = e.message ?: "Data integrity error", originalCause = e))
        } catch (e: FirebaseAuthInvalidUserException) {
            // User account doesn't exist
            Result.failure(UserNotFoundException(originalCause = e))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Wrong password or invalid credentials
            Result.failure(InvalidCredentialsException(originalCause = e))
        } catch (e: FirebaseAuthUserCollisionException) {
            // Email already registered
            Result.failure(EmailAlreadyInUseException(originalCause = e))
        } catch (e: FirebaseAuthWeakPasswordException) {
            // Password doesn't meet Firebase security requirements
            Result.failure(WeakPasswordException(originalCause = e))
        } catch (e: InvalidUserDataException) {
            // Already a domain exception from data layer validation
            Result.failure(e)
        } catch (e: IllegalArgumentException) {
            // Invalid arguments passed to data layer
            Result.failure(
                DomainException.InvalidData(
                    reason = e.message ?: "Invalid data for $operation",
                    originalCause = e
                )
            )
        } catch (e: Exception) {
            // Unexpected error - wrap in generic domain exception
            Result.failure(
                DomainException.Unknown(
                    reason = "Unexpected error during $operation: ${e.message}",
                    originalCause = e
                )
            )
        }
    }
}