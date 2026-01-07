package com.example.munchly.data.remote

import android.util.Log
import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.domain.exceptions.DataIntegrityException
import com.example.munchly.domain.exceptions.DataUsernameConflictException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

// ============================================================================
// FIRESTORE COLLECTION NAMES
// ============================================================================

private const val COLLECTION_USERS = "users"

// ============================================================================
// LOGGING TAG
// ============================================================================

private const val TAG = "AuthRemoteDataSource"

// ============================================================================
// REMOTE DATA SOURCE INTERFACE
// ============================================================================

/**
 * Interface defining remote data operations for authentication.
 * Abstracts Firebase-specific implementation details from the repository layer.
 */
interface AuthRemoteDataSource {

    /**
     * Authenticates user with email and password.
     * @return User data transfer object
     * @throws Exception on authentication or network errors
     */
    suspend fun login(email: String, password: String): User

    /**
     * Creates a new user account.
     * @return Created user data transfer object
     * @throws DataUsernameConflictException if username already exists
     * @throws Exception on registration or network errors
     */
    suspend fun register(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): User
}

// ============================================================================
// FIREBASE IMPLEMENTATION
// ============================================================================

/**
 * Firebase implementation of AuthRemoteDataSource.
 * Handles all Firebase-specific logic including:
 * - Firebase Auth for user authentication
 * - Firestore for user data persistence
 * - Data integrity validation
 * - Rollback on partial failure
 */
class AuthRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRemoteDataSource {

    override suspend fun login(email: String, password: String): User {
        // Defensive programming: validate inputs at boundary
        validateLoginInput(email, password)

        // Step 1: Authenticate with Firebase Auth
        val authResult = auth.signInWithEmailAndPassword(email, password).await()

        val firebaseUser = authResult.user
            ?: throw DataIntegrityException(
                "Authentication succeeded but Firebase returned null user. " +
                        "This indicates a Firebase SDK issue. Email: $email"
            )

        // Step 2: Fetch user data from Firestore
        val userDocument = firestore
            .collection(COLLECTION_USERS)
            .document(firebaseUser.uid)
            .get()
            .await()

        // Step 3: Deserialize and validate user data
        val user = userDocument.toObject(User::class.java)
            ?: throw DataIntegrityException(
                "User document exists in Firestore but failed to deserialize. " +
                        "UID: ${firebaseUser.uid}, Document data: ${userDocument.data}"
            )

        // Data layer validates its own outputs before returning to domain
        validateUserData(user)

        return user
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): User {
        // Defensive programming: validate inputs at boundary
        validateRegisterInput(email, password, username)

        // Step 1: Check if username already exists
        // Note: This has a potential race condition. For production,
        // consider using Firestore transactions or unique index constraints.
        try {
            val usernameQuery = firestore
                .collection(COLLECTION_USERS)
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()

            if (!usernameQuery.isEmpty) {
                throw DataUsernameConflictException(username)
            }
        } catch (e: DataUsernameConflictException) {
            // Re-throw username conflict directly
            throw e
        } catch (e: FirebaseFirestoreException) {
            // Network or Firestore issues during username check
            Log.e(TAG, "Failed to check username uniqueness for: $username", e)
            throw Exception(
                "Failed to verify username availability. Please try again. " +
                        "Error: ${e.message}",
                e
            )
        }

        // Step 2: Create user in Firebase Auth
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()

        val firebaseUser = authResult.user
            ?: throw DataIntegrityException(
                "User creation succeeded but Firebase returned null user. " +
                        "This indicates a Firebase SDK issue. Email: $email"
            )

        // Step 3: Create user document
        val user = User(
            uid = firebaseUser.uid,
            email = email,
            userType = userType,
            username = username,
            name = null,
            createdAt = System.currentTimeMillis()
        )

        // Data layer validates its own data before persisting
        validateUserData(user)

        // Step 4: Save to Firestore with rollback on failure
        try {
            firestore
                .collection(COLLECTION_USERS)
                .document(firebaseUser.uid)
                .set(user)
                .await()
        } catch (e: Exception) {
            // Firestore save failed - attempt to rollback Firebase Auth user creation
            Log.e(TAG, "Failed to save user to Firestore, attempting rollback. UID: ${firebaseUser.uid}", e)

            try {
                auth.currentUser?.delete()?.await()
                Log.i(TAG, "Successfully rolled back auth user creation. UID: ${firebaseUser.uid}")
            } catch (deleteException: Exception) {
                // Rollback failed - log extensively for monitoring and alerting
                Log.e(
                    TAG,
                    "CRITICAL: Failed to rollback auth user after Firestore save failure. " +
                            "Orphaned account created. UID: ${firebaseUser.uid}, Email: $email",
                    deleteException
                )
                // In production: send to error tracking service (Sentry, Crashlytics, etc.)
            }

            // Re-throw original exception with user-friendly context
            throw Exception(
                "Failed to create user account. Please try again. " +
                        "If problem persists, contact support. Error: ${e.message}",
                e
            )
        }

        return user
    }

    // ========================================================================
    // VALIDATION HELPERS
    // ========================================================================

    /**
     * Validates login inputs at data layer boundary.
     * Ensures the data layer receives valid data from calling code.
     * Blank inputs indicate a bug in the use case layer.
     */
    private fun validateLoginInput(email: String, password: String) {
        when {
            email.isBlank() -> throw IllegalArgumentException(
                "Email cannot be blank. This indicates a bug in the calling code."
            )
            password.isBlank() -> throw IllegalArgumentException(
                "Password cannot be blank. This indicates a bug in the calling code."
            )
        }
    }

    /**
     * Validates registration inputs at data layer boundary.
     * Ensures the data layer receives valid data from calling code.
     * Blank inputs indicate a bug in the use case layer.
     */
    private fun validateRegisterInput(email: String, password: String, username: String) {
        when {
            email.isBlank() -> throw IllegalArgumentException(
                "Email cannot be blank. This indicates a bug in the calling code."
            )
            password.isBlank() -> throw IllegalArgumentException(
                "Password cannot be blank. This indicates a bug in the calling code."
            )
            username.isBlank() -> throw IllegalArgumentException(
                "Username cannot be blank. This indicates a bug in the calling code."
            )
        }
    }

    /**
     * Validates User DTO has all required fields populated.
     * Data layer is responsible for ensuring data integrity before
     * returning to domain layer or before persisting to database.
     */
    private fun validateUserData(user: User) {
        when {
            user.uid.isBlank() -> throw DataIntegrityException(
                "User UID is blank. This indicates corrupted data from Firebase."
            )
            user.email.isBlank() -> throw DataIntegrityException(
                "User email is blank. This indicates corrupted data from Firebase."
            )
            user.username.isBlank() -> throw DataIntegrityException(
                "Username is blank. This indicates corrupted data from Firebase."
            )
        }
    }
}