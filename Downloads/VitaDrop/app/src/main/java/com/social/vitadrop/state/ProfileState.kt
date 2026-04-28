package com.social.vitadrop.state

data class ProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val role: String = "",
    val message: String = ""
)