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

            val userData = hashMapOf(
                "name" to user.name,
                "email" to user.email,
                "phone" to user.phone,
                "role" to user.role
            )

            db.collection("users").document(uid).set(userData).await()

            when (user.role) {
                "donor" -> {
                    db.collection("donors").document(uid).set(
                        mapOf(
                            "bloodGroup" to user.bloodGroup,
                            "city" to user.city,
                            "available" to true
                        )
                    ).await()
                }

                "hospital" -> {
                    db.collection("hospitals").document(uid).set(
                        mapOf(
                            "hospitalName" to user.name,
                            "address" to user.address,
                            "city" to user.city,
                            "verified" to false
                        )
                    ).await()
                }

                "admin" -> {
                    db.collection("admin").document(uid)
                        .set(mapOf("name" to user.name)).await()
                }
            }

            Result.success(uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
