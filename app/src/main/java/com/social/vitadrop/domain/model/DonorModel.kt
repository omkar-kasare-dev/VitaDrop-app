package com.social.vitadrop.domain.model
/*
data class DonorModel(
    val id: String = "",
    val name: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val phone: String = "",
    val isAvailable: Boolean = true
)

 */

// Modified Donor Model:


data class DonorModel(

    // BASIC INFO
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",

    // PERSONAL INFO
    val gender: String = "",
    val age: Int = 0,

    // BLOOD INFO
    val bloodGroup: String = "",

    // ADDRESS INFO
    val city: String = "",
    val state: String = "",
    val address: String = "",

    // LOCATION INFO
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // PROFILE INFO
    val profileImage: String = "",
    val weight: Double = 0.0,

    // STATUS INFO
    val isAvailable: Boolean = true,
    val isVerified: Boolean = false,
    val isBlocked: Boolean = false,

    // DEVICE INFO
    val devicePlatform: String = "android",
    val fcmToken: String = "",

    // TIMESTAMPS
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val lastActive: Long = 0L
)