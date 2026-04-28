package com.social.vitadrop.domain.usecase



import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.repository.AuthRepository

class RegisterUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: User, password: String): Result<String> {
        return repository.registerUser(user, password)
    }
}