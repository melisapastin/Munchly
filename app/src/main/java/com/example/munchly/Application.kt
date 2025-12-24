package com.example.munchly

import android.app.Application
import com.example.munchly.data.remote.LoginRemoteDataSourceImpl
import com.example.munchly.data.remote.RegisterRemoteDataSourceImpl
import com.example.munchly.data.repository.LoginRepositoryImpl
import com.example.munchly.data.repository.RegisterRepositoryImpl
import com.example.munchly.domain.usecases.LoginUseCase
import com.example.munchly.domain.usecases.RegisterUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MunchlyApplication : Application() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val loginUseCase: LoginUseCase by lazy {
        val remoteDataSource =
            LoginRemoteDataSourceImpl(firebaseAuth, firestore)

        val repository =
            LoginRepositoryImpl(remoteDataSource)

        LoginUseCase(repository)
    }

    val registerUseCase: RegisterUseCase by lazy {
        val remoteDataSource =
            RegisterRemoteDataSourceImpl(firebaseAuth, firestore)

        val repository =
            RegisterRepositoryImpl(remoteDataSource)

        RegisterUseCase(repository)
    }
}
