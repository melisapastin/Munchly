package com.example.munchly.data.repository

import com.example.munchly.data.models.RegistrationData
import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Authentication failed"))

            val userType = getUserType(firebaseUser.uid)
            Result.success(firebaseUser.toDomainUser(userType))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Google authentication failed"))

            val userType = getUserType(firebaseUser.uid)
            if (authResult.additionalUserInfo?.isNewUser == true) {
                saveUserType(firebaseUser.uid, UserType.FOOD_LOVER)
            }

            Result.success(firebaseUser.toDomainUser(userType))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(registrationData: RegistrationData): Result<User> {
        return try {
            // Create user in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(
                registrationData.email,
                registrationData.password
            ).await()

            val firebaseUser = authResult.user ?: return Result.failure(Exception("Registration failed"))

            // Update profile with username
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(registrationData.username)
                .build()

            firebaseUser.updateProfile(profileUpdates).await()

            // Save user type to Firestore
            saveUserType(firebaseUser.uid, registrationData.userType)

            Result.success(firebaseUser.toDomainUser(registrationData.userType, registrationData.username))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getUserType(uid: String): UserType {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            when (snapshot.getString("userType")) {
                "RESTAURANT_OWNER" -> UserType.RESTAURANT_OWNER
                else -> UserType.FOOD_LOVER
            }
        } catch (e: Exception) {
            UserType.FOOD_LOVER
        }
    }

    private suspend fun saveUserType(uid: String, userType: UserType) {
        firestore.collection("users").document(uid).set(
            mapOf(
                "userType" to userType.name,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
        ).await()
    }

    private fun com.google.firebase.auth.FirebaseUser.toDomainUser(userType: UserType, username: String? = null): User {
        return User(
            uid = uid,
            email = email ?: "",
            userType = userType,
            username = username ?: displayName,
            name = displayName
        )
    }
}