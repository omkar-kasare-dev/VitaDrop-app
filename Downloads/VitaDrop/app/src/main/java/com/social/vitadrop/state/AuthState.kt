package com.social.vitadrop.state

data class AuthState(
    val email: String = "",
    val password: String = "",
    val role: String = "",
    val message: String = "",
    val isLoading: Boolean = false
)