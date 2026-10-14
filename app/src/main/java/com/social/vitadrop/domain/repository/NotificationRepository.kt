package com.social.vitadrop.domain.repository


import com.social.vitadrop.domain.model.UserRole

interface NotificationRepository {
    /** Save this device's push token on the user's profile (and join the donors topic for donors). */
    suspend fun registerDevice(role: UserRole, knownToken: String? = null): Result<Unit>

    /** Best effort and never throws: remove the token from the profile and from this device. */
    suspend fun unregisterDevice(role: UserRole?)
}