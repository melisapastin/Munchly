package com.example.munchly.data.repository

import com.example.munchly.data.models.User
import com.example.munchly.data.remote.LoginRemoteDataSource

interface LoginRepository {

    suspend fun loginUser(
        email: String,
        password: String
    ): Result<User>
}

class LoginRepositoryImpl(
    private val loginRemoteDataSource: LoginRemoteDataSource
) : LoginRepository {

    override suspend fun loginUser(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val user = loginRemoteDataSource.loginUser(email, password)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
