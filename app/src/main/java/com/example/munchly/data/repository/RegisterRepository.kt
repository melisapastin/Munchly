package com.example.munchly.data.repository

import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.data.remote.RegisterRemoteDataSource

interface RegisterRepository {

    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): Result<User>
}

class RegisterRepositoryImpl(
    private val registerRemoteDataSource: RegisterRemoteDataSource
) : RegisterRepository {

    override suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): Result<User> {
        return try {
            val user = registerRemoteDataSource.registerUser(
                email = email,
                password = password,
                username = username,
                userType = userType
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
