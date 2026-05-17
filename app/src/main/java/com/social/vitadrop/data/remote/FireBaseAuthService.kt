package com.social.vitadrop.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.User
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Registration service
    suspend fun register(user: User, password: String): Result<String> {
        return try {

            if (user.email.isBlank() || password.length < 6) {
                return Result.failure(Exception("Invalid email or password"))
            }

            val result = auth.createUserWithEmailAndPassword(user.email, password).await()

            val uid = result.user?.uid
                ?: return Result.failure(Exception("User ID is null"))
/*
            val userData = hashMapOf(
                "name" to user.name,
                "email" to user.email,
                "phone" to user.phone,
                "role" to user.role
            )

            db.collection("users").document(uid).set(userData).await()


 */
            when (user.role) {
                "donor" -> {
                    /*
                    db.collection("donors").document(uid).set(
                        mapOf(
                            "uid" to uid,
                            "name" to user.name,
                            "email" to user.email,
                            "phone" to user.phone,
                            "bloodGroup" to user.bloodGroup,
                            "city" to user.city,
                            "available" to true
                        )
                    ).await()

                     */

                    // Modified:
                    db.collection("donors").document(uid).set(

                        mapOf(

                            // Basic Details
                            "uid" to uid,
                            "fullName" to user.fullName,
                            "email" to user.email,
                            "phone" to user.phone,
                            "gender" to user.gender,
                            "age" to user.age,

                            // Blood Details
                            "bloodGroup" to user.bloodGroup,

                            // Address Details
                            "city" to user.city,
                            "state" to user.state,
                            "address" to user.address,

                            // Geo Location
                            "location" to mapOf(
                                "latitude" to user.latitude,
                                "longitude" to user.longitude
                            ),

                            // Profile
                            "profileImage" to user.profileImage,
                            "weight" to user.weight,

                            // Donation Info
                            "lastDonationDate" to user.lastDonationDate,

                            // Status
                            "isAvailable" to true,
                            "isVerified" to false,
                            "isBlocked" to false,

                            // Device / Notification
                            "devicePlatform" to "android",
                            "fcmToken" to user.fcmToken,

                            // Timestamps
                            "createdAt" to com.google.firebase.Timestamp.now(),
                            "updatedAt" to com.google.firebase.Timestamp.now(),
                            "lastActive" to com.google.firebase.Timestamp.now()

                        )

                    ).await()
                }

                // Hospital Section Start:

                "hospital" -> {

                    db.collection("hospitals").document(uid).set(

                        mapOf(

                            // Basic Details
                            "uid" to uid,
                            "hospitalName" to user.fullName,
                            "email" to user.email,
                            "phone" to user.phone,

                            // Hospital Details
                            "licenseNumber" to user.licenseNumber,

                            // Address Details
                            "city" to user.city,
                            "state" to user.state,
                            "address" to user.address,

                            // Location
                            "location" to mapOf(
                                "latitude" to user.latitude,
                                "longitude" to user.longitude
                            ),

                            // Profile
                            "profileImage" to user.profileImage,

                            // Status
                            "isVerified" to false,
                            "isBlocked" to false,

                            // Notifications
                            "fcmToken" to user.fcmToken,

                            // Timestamps
                            "createdAt" to com.google.firebase.Timestamp.now(),
                            "updatedAt" to com.google.firebase.Timestamp.now()

                        )

                    ).await()
                }

                // Hospital Section END:
/*

                "admin" -> {
                    db.collection("admin").document(uid)
                        .set(mapOf("name" to user.name)).await()
                }

 */
            }

            Result.success(uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
