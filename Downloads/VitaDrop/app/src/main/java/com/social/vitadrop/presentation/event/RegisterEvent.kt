package com.social.vitadrop.presentation.event

sealed class RegisterEvent {
    data class UpdateField(
        val name: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val bloodGroup: String? = null,
        val city: String? = null,
        val address: String? = null,
        val password: String? = null,
        val role: String? = null
    ) : RegisterEvent()

    object Register : RegisterEvent()
}