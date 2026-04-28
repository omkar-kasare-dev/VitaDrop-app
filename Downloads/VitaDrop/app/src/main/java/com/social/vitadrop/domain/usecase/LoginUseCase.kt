package com.social.vitadrop.domain.usecase

import com.social.vitadrop.domain.repository.AuthRepository
class LoginUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<String> {
        return repository.login(email, password)
    }
}