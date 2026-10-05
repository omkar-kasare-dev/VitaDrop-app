package com.social.vitadrop.domain.model

data class User(
    val fullName: String,
    val email: String,
    val role: UserRole,
    val uid: String = "",
    val phone: String = "",
    val gender: String = "",
    val age: Int = 0,
    val bloodGroup: String = "",
    val weight: Double = 0.0,
    val city: String = "",
    val state: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val profileImage: String = "",
    val lastDonationDateMillis: Long? = null,
    val licenseNumber: String = "",
    val fcmToken: String = ""
)