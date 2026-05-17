package com.social.vitadrop.domain.model


/*
data class User(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val address: String = ""
)


 */
// Modified Model:



import com.google.firebase.Timestamp

data class User(

    // Basic Details
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val age: Int = 0,

    // Role
    val role: String = "",

    // Blood Details
    val bloodGroup: String = "",

    // Address Details
    val city: String = "",
    val state: String = "",
    val address: String = "",

    // Location
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // Profile
    val profileImage: String = "",
    val weight: Double = 0.0,

    // Donation Info
    val lastDonationDate: Timestamp? = null,

    // Status
    val isAvailable: Boolean = true,
    val isVerified: Boolean = false,
    val isBlocked: Boolean = false,

    // Device Info
    val devicePlatform: String = "android",
    val fcmToken: String = "",

    // Timestamps
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val lastActive: Timestamp? = null,

 // Hospital
    val licenseNumber: String=""

)