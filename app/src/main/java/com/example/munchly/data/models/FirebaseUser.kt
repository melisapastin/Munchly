package com.example.munchly.data.models

import com.google.firebase.auth.FirebaseUser as FirebaseAuthUser

fun FirebaseAuthUser.toDomainUser(userType: UserType): User {
    return User(
        uid = uid,
        email = email ?: "",
        userType = userType,
        name = displayName
    )
}