package com.social.vitadrop.data.mapper

import com.google.firebase.Timestamp
import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.model.UserRole
import java.util.Date

val UserRole.collectionName: String
    get() = when (this) {
        UserRole.DONOR -> "donors"
        UserRole.HOSPITAL -> "hospitals"
    }

fun User.toFirestoreMap(uid: String): Map<String, Any?> {
    val now = Timestamp.now()
    return when (role) {
        UserRole.DONOR -> mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "gender" to gender,
            "age" to age,
            "bloodGroup" to bloodGroup,
            "city" to city,
            "state" to state,
            "address" to address,
            "location" to mapOf("latitude" to latitude, "longitude" to longitude),
            "profileImage" to profileImage,
            "weight" to weight,
            "lastDonationDate" to lastDonationDateMillis?.let { Timestamp(Date(it)) },
            "isAvailable" to true,
            "isVerified" to false,
            "isBlocked" to false,
            "devicePlatform" to "android",
            "fcmToken" to fcmToken,
            "createdAt" to now,
            "updatedAt" to now,
            "lastActive" to now
        )

        UserRole.HOSPITAL -> mapOf(
            "uid" to uid,
            "hospitalName" to fullName,
            "email" to email,
            "phone" to phone,
            "licenseNumber" to licenseNumber,
            "city" to city,
            "state" to state,
            "address" to address,
            "location" to mapOf("latitude" to latitude, "longitude" to longitude),
            "profileImage" to profileImage,
            "isVerified" to false,
            "isBlocked" to false,
            "fcmToken" to fcmToken,
            "createdAt" to now,
            "updatedAt" to now
        )
    }
}