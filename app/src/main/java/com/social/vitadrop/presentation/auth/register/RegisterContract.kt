package com.social.vitadrop.presentation.auth.register

import com.social.vitadrop.domain.model.UserRole

data class RegisterState(
    val role: UserRole? = null,
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val phone: String = "",
    val gender: String = "",
    val age: String = "",
    val bloodGroup: String = "",
    val weight: String = "",
    val licenseNumber: String = "",
    val city: String = "",
    val state: String = "",
    val address: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class RegisterField {
    EMAIL, PASSWORD, FULL_NAME, PHONE, GENDER, AGE, BLOOD_GROUP,
    WEIGHT, LICENSE, CITY, STATE, ADDRESS
}

sealed interface RegisterIntent {
    data class SelectRole(val role: UserRole) : RegisterIntent
    data class FieldChanged(val field: RegisterField, val value: String) : RegisterIntent
    data class LocationReceived(val lat: Double, val lng: Double) : RegisterIntent
    data object Submit : RegisterIntent
}

sealed interface RegisterEffect {
    data object RegistrationSuccess : RegisterEffect
}