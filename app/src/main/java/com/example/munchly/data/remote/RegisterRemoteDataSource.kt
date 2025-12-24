package com.example.munchly.data.remote

import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.domain.usecases.AuthException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.tasks.await

interface RegisterRemoteDataSource {
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): User
}

class RegisterRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RegisterRemoteDataSource {

    override suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): User {
        // 1. Check if username already exists
        val usernameQuery = try {
            firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()
        } catch (e: FirebaseNetworkException) {
            throw AuthException.NetworkError
        } catch (e: Exception) {
            throw AuthException.Unknown(e.message ?: "Failed to check username")
        }

        if (!usernameQuery.isEmpty) {
            throw AuthException.UsernameAlreadyTaken
        }

        // 2. Create user in Firebase Auth
        val authResult = try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthException.EmailAlreadyInUse
        } catch (e: FirebaseAuthWeakPasswordException) {
            throw AuthException.WeakPassword
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException.Unknown("Invalid email format")
        } catch (e: FirebaseNetworkException) {
            throw AuthException.NetworkError
        } catch (e: Exception) {
            throw AuthException.Unknown(e.message ?: "Registration failed")
        }

        val firebaseUser = authResult.user
            ?: throw AuthException.Unknown("User creation failed")

        // 3. Create user document
        val user = User(
            uid = firebaseUser.uid,
            email = email,
            userType = userType,
            username = username
        )

        // 4. Save to Firestore
        try {
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()
        } catch (e: FirebaseNetworkException) {
            throw AuthException.NetworkError
        } catch (e: Exception) {
            throw AuthException.Unknown(e.message ?: "Failed to save user data")
        }

        return user
    }
}