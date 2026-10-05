package com.social.vitadrop.presentation.auth.login

import com.social.vitadrop.domain.model.UserRole

// STATE: everything the screen draws
data class LoginState(
    val role: UserRole? = null,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// INTENT: everything the user can do
sealed interface LoginIntent {
    data class SelectRole(val role: UserRole) : LoginIntent
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object Submit : LoginIntent
}

// EFFECT: one-shot events (never stored in State)
sealed interface LoginEffect {
    data class LoggedIn(val role: UserRole) : LoginEffect
}