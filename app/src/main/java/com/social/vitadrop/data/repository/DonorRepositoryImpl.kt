package com.social.vitadrop.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.DonorModel
import com.social.vitadrop.domain.repository.DonorRepository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

class DonorRepositoryImpl : DonorRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun getAllDonors(): Result<List<DonorModel>> {
        return try {
            val snapshot = withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("donors").get().await()
            }

            if (snapshot == null) {
                Result.failure<List<DonorModel>>(
                    IOException("Could not reach the server. Check your internet connection and try again.")
                )
            } else {
                Result.success(snapshot.documents.map { it.toDonorModel() })
            }

        } catch (e: Exception) {
            // Before: the error was hidden and an empty list returned ("No donors found").
            // Now the error travels up so the screen can show it with a Retry button.
            Log.e("DONOR_REPO", "Failed to load donors", e)
            Result.failure(e)
        }
    }

    private fun DocumentSnapshot.toDonorModel(): DonorModel {
        val location = get("location") as? Map<*, *>

        return DonorModel(
            // BASIC INFO
            uid = getString("uid") ?: id,
            fullName = getString("fullName") ?: "",
            email = getString("email") ?: "",
            phone = getString("phone") ?: "",

            // PERSONAL INFO
            gender = getString("gender") ?: "",
            age = getLong("age")?.toInt() ?: 0,

            // BLOOD INFO
            bloodGroup = getString("bloodGroup") ?: "",

            // ADDRESS INFO
            city = getString("city") ?: "",
            state = getString("state") ?: "",
            address = getString("address") ?: "",

            // LOCATION (was read before but never assigned)
            latitude = (location?.get("latitude") as? Number)?.toDouble() ?: 0.0,
            longitude = (location?.get("longitude") as? Number)?.toDouble() ?: 0.0,

            // PROFILE
            profileImage = getString("profileImage") ?: "",
            weight = getDouble("weight") ?: 0.0,

            // STATUS
            isAvailable = getBoolean("isAvailable") ?: true,
            isVerified = getBoolean("isVerified") ?: false,
            isBlocked = getBoolean("isBlocked") ?: false,

            // DEVICE INFO
            devicePlatform = getString("devicePlatform") ?: "",
            fcmToken = getString("fcmToken") ?: ""
        )
    }

    private companion object {
        const val TIMEOUT_MS = 15_000L
    }
}