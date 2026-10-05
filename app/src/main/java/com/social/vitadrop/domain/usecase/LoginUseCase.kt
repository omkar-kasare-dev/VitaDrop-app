package com.social.vitadrop.domain.usecase

import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        role: UserRole
    ): Result<String> = repository.login(email.trim(), password, role)
}