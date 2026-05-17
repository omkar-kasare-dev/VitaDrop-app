package com.social.vitadrop.presentation.event
/*
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


 */

// Updated  RegisterEvent:
sealed class RegisterEvent {

    data class UpdateField(

        // Basic Details
        val uid: String? = null,
        val fullName: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val gender: String? = null,
        val age: String? = null,

        // Blood Details
        val bloodGroup: String? = null,

        // Address Details
        val city: String? = null,
        val state: String? = null,
        val address: String? = null,

        // Location
        val latitude: String? = null,
        val longitude: String? = null,

        // Profile
        val profileImage: String? = null,
        val weight: String? = null,

        // Authentication
        val password: String? = null,
        val role: String? = null,

        // Status
        val isAvailable: Boolean? = null,
        val isVerified: Boolean? = null,
        val isBlocked: Boolean? = null,

        // Device
        val devicePlatform: String? = null,
        val fcmToken: String? = null,

        // Hospital Licence number Event:
        val licenseNumber: String?=null

    ) : RegisterEvent()

    object Register : RegisterEvent()
}