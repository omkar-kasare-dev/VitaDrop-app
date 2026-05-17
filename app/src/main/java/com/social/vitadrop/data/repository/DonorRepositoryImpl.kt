package com.social.vitadrop.data.repository

/*
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.DonorModel
import com.social.vitadrop.domain.repository.DonorRepository
import kotlinx.coroutines.tasks.await

class DonorRepositoryImpl : DonorRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun getAllDonors(): List<DonorModel> {

        return try {

            val result = db.collection("donors").get().await()

            result.map { doc ->

                DonorModel(
                    uid = doc.id,
                    fullName = doc.getString("name") ?: "",
                    bloodGroup = doc.getString("bloodGroup") ?: "",
                    city = doc.getString("city") ?: "",
                    phone = doc.getString("contactNumber") ?: "",
                    isAvailable = doc.getBoolean("isAvailable") ?: true
                )
            }

        } catch (e: Exception) {
            Log.e("DONOR_ERROR", e.message ?: "")
            emptyList()
        }
    }
}

 */

// Modified Donor repository.



import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.DonorModel
import com.social.vitadrop.domain.repository.DonorRepository
import kotlinx.coroutines.tasks.await

class DonorRepositoryImpl : DonorRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun getAllDonors(): List<DonorModel> {

        return try {

            val result = db.collection("donors")
                .get()
                .await()

            result.map { doc ->

                val location =
                    doc.get("location") as? Map<*, *>

                DonorModel(

                    // BASIC INFO
                    uid = doc.getString("uid") ?: doc.id,

                    fullName = doc.getString("fullName") ?: "",

                    email = doc.getString("email") ?: "",

                    phone = doc.getString("phone") ?: "",

                    gender = doc.getString("gender") ?: "",

                    age = doc.getLong("age")?.toInt() ?: 0,

                    // BLOOD INFO
                    bloodGroup = doc.getString("bloodGroup") ?: "",

                    // ADDRESS INFO
                    city = doc.getString("city") ?: "",

                    state = doc.getString("state") ?: "",

                    address = doc.getString("address") ?: "",



                    // PROFILE
                    profileImage = doc.getString("profileImage") ?: "",

                    weight = doc.getDouble("weight") ?: 0.0,

                    // STATUS
                    isAvailable = doc.getBoolean("isAvailable") ?: true,

                    isVerified = doc.getBoolean("isVerified") ?: false,

                    isBlocked = doc.getBoolean("isBlocked") ?: false,

                    // DEVICE INFO
                    devicePlatform = doc.getString("devicePlatform") ?: "",

                    fcmToken = doc.getString("fcmToken") ?: ""
                )
            }

        } catch (e: Exception) {

            Log.e(
                "DONOR_ERROR",
                e.message ?: "Unknown Error"
            )

            emptyList()
        }
    }
}