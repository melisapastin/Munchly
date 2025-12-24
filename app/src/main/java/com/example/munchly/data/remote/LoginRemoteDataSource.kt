package com.example.munchly.data.remote

import com.example.munchly.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.example.munchly.domain.usecases.AuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.tasks.await

interface LoginRemoteDataSource {
    suspend fun loginUser(email: String, password: String): User
}

class LoginRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : LoginRemoteDataSource {

    override suspend fun loginUser(email: String, password: String): User {
        // 1. Authenticate with Firebase Auth
        val authResult = try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthException.UserNotFound
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException.InvalidCredentials
        } catch (e: FirebaseNetworkException) {
            throw AuthException.NetworkError
        } catch (e: Exception) {
            throw AuthException.Unknown(e.message ?: "Authentication failed")
        }

        val firebaseUser = authResult.user
            ?: throw AuthException.InvalidCredentials

        // 2. Fetch user data from Firestore
        val userDocument = try {
            firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()
        } catch (e: FirebaseNetworkException) {
            throw AuthException.NetworkError
        } catch (e: Exception) {
            throw AuthException.Unknown(e.message ?: "Failed to fetch user data")
        }

        // 3. Convert to User model and validate
        val user = userDocument.toObject(User::class.java)
            ?: throw AuthException.UserDataNotFound

        if (user.uid.isBlank() || user.email.isBlank()) {
            throw AuthException.InvalidUserData
        }

        return user
    }
}