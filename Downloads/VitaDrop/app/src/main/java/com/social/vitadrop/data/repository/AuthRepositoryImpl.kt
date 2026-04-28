package com.social.vitadrop.data.repository

import com.social.vitadrop.data.remote.FirebaseAuthService
import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val service: FirebaseAuthService
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return service.login(email, password)
    }

    override suspend fun registerUser(
        user: User,
        password: String
    ): Result<String> {
        return service.register(user, password)
    }
}