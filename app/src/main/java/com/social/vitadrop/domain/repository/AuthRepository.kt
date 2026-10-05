package com.social.vitadrop.domain.repository

import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.model.UserRole

interface AuthRepository {

    suspend fun login(email: String, password: String, role: UserRole): Result<String>

    suspend fun registerUser(
        user: User,
        password: String
    ): Result<String>
}

