package com.example.munchly.domain.exceptions

// ============================================================================
// AUTHENTICATION EXCEPTIONS
// ============================================================================
// These exceptions extend DomainException to maintain consistency while
// keeping auth-specific errors in a separate file for better organization.
// ============================================================================

/**
 * User not found during login (invalid email).
 */
class UserNotFoundException(
    originalCause: Throwable? = null
) : DomainException(
    message = "User not found in authentication system",
    cause = originalCause
)

/**
 * Invalid login credentials (wrong password).
 */
class InvalidCredentialsException(
    originalCause: Throwable? = null
) : DomainException(
    message = "Invalid credentials provided for authentication",
    cause = originalCause
)

/**
 * Email already registered during signup.
 */
class EmailAlreadyInUseException(
    originalCause: Throwable? = null
) : DomainException(
    message = "Email address already registered in system",
    cause = originalCause
)

/**
 * Username already taken during signup.
 */
class UsernameCollisionException(
    originalCause: Throwable? = null
) : DomainException(
    message = "Username already exists in system",
    cause = originalCause
)

/**
 * Password does not meet security requirements.
 */
class WeakPasswordException(
    originalCause: Throwable? = null
) : DomainException(
    message = "Password does not meet security requirements",
    cause = originalCause
)

/**
 * User data missing or not found in database after authentication.
 */
class UserDataNotFoundException(
    originalCause: Throwable? = null
) : DomainException(
    message = "User data not found in database after authentication",
    cause = originalCause
)

/**
 * User data is invalid or corrupted.
 */
class InvalidUserDataException(
    val details: String = "User data validation failed",
    originalCause: Throwable? = null
) : DomainException(
    message = "Invalid user data: $details",
    cause = originalCause
)

// ============================================================================
// DATA LAYER EXCEPTIONS
// ============================================================================
// Internal exceptions used by data layer for specific error conditions.
// Repository layer catches these and maps them to appropriate domain exceptions.
// ============================================================================

/**
 * Thrown when attempting to register with a username that already exists.
 * Internal to data layer - repository maps this to UsernameCollisionException.
 */
internal class DataUsernameConflictException(
    val username: String
) : Exception("Username conflict in database: $username")

/**
 * Thrown when data returned from remote source is invalid or corrupted.
 * Internal to data layer - repository maps this to InvalidUserDataException.
 */
internal class DataIntegrityException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)