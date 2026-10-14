package com.social.vitadrop.presentation.profile

import com.social.vitadrop.domain.model.UserProfile

// STATE
data class ProfileState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,     // before: "message", which no screen ever displayed
    val isLoggingOut: Boolean = false
)

// INTENT
sealed interface ProfileIntent {
    data object Refresh : ProfileIntent   // also used by the Retry button
    data object Logout : ProfileIntent
}

// EFFECT: one-shot navigation event
sealed interface ProfileEffect {
    data object LoggedOut : ProfileEffect
}