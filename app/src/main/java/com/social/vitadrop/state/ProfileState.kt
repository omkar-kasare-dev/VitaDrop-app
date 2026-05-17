package com.social.vitadrop.state
/*
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

 */

// Updated ProfileState


data class ProfileState(

    // Loading & Message
    val isLoading: Boolean = false,
    val message: String = "",

    // Basic User Details
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "",

    // Donor Details
    val gender: String = "",
    val age: String = "",
    val bloodGroup: String = "",
    val weight: String = "",

    // Address Details
    val city: String = "",
    val state: String = "",
    val address: String = "",

    // Location
    val latitude: String = "",
    val longitude: String = "",

    // Hospital Details
    val licenseNumber: String = "",

    // Profile
    val profileImage: String = "",

    // Status
    val isAvailable: Boolean = false,
    val isVerified: Boolean = false,
    val isBlocked: Boolean = false
)