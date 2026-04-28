package com.social.vitadrop.domain.repository

import com.social.vitadrop.domain.model.User

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<String>

    suspend fun registerUser(
        user: User,
        password: String
    ): Result<String>
}