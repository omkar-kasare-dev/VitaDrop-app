package com.social.vitadrop.state


/*
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

 */

// Updated register State.
data class RegisterState(

    // Basic Details
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val age: String = "",

    // Blood Details
    val bloodGroup: String = "",

    // Address Details
    val city: String = "",
    val state: String = "",
    val address: String = "",

    // Location
    val latitude: String = "",
    val longitude: String = "",

    // Profile
    val profileImage: String = "",
    val weight: String = "",

    // Authentication
    val password: String = "",
    val role: String = "",

    // Status
    val isAvailable: Boolean = true,
    val isVerified: Boolean = false,
    val isBlocked: Boolean = false,

    // Device
    val devicePlatform: String = "android",
    val fcmToken: String = "",

    // UI State
    val isLoading: Boolean = false,
    val message: String = "",

    // Hospotal State:
    val licenseNumber:String=""
)