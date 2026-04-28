package com.social.vitadrop.state



data class RegisterState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val address: String = "",
    val password: String = "",
    val role: String = "",
    val isLoading: Boolean = false,
    val message: String = ""
)