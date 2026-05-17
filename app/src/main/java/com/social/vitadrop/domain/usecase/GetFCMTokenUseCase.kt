package com.social.vitadrop.domain.usecase



import com.social.vitadrop.domain.repository.NotificationRepository

class GetFCMTokenUseCase(
    private val repository: NotificationRepository
) {

    suspend operator fun invoke(): String {

        return repository.getFCMToken()
    }
}