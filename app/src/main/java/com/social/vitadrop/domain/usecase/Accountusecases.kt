package com.social.vitadrop.domain.usecase


import com.social.vitadrop.domain.model.UserProfile
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.repository.AuthRepository
import com.social.vitadrop.domain.repository.NotificationRepository
import com.social.vitadrop.domain.repository.ProfileRepository

/** Loads the profile of the logged-in user. */
class GetProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> = repository.getProfile()
}

/**
 * Logs the user out of this device.
 * Order matters: the device token must be removed from the user's profile while the user is
 * still signed in, otherwise the Firestore write would be rejected. After that the user is
 * signed out. Removing the token never blocks or fails the logout.
 */
class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(role: UserRole?) {
        notificationRepository.unregisterDevice(role)
        authRepository.logout()
    }
}

/**
 * Saves this device's push token on the user's profile (donors/{uid} or hospitals/{uid}).
 * Pass [knownToken] when Firebase already gave you the new token (onNewToken).
 */
class RegisterDeviceUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(role: UserRole, knownToken: String? = null): Result<Unit> =
        repository.registerDevice(role, knownToken)
}