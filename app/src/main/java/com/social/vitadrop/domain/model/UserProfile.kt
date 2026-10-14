package com.social.vitadrop.domain.model

/**
 * Pure domain model of a logged-in user's profile (no Firebase types, no strings for numbers).
 * Donor-only and hospital-only fields keep neutral defaults for the other role.
 */
data class UserProfile(
    val uid: String,
    val role: UserRole,
    val fullName: String,          // donor name, or the hospital name for hospitals
    val email: String,
    val phone: String,
    val city: String,
    val state: String,
    val address: String,
    val profileImage: String,
    val isVerified: Boolean,
    val isBlocked: Boolean,

    // donor only
    val gender: String = "",
    val age: Int = 0,
    val bloodGroup: String = "",
    val weightKg: Double = 0.0,
    val isAvailable: Boolean = false,

    // hospital only
    val licenseNumber: String = ""
)