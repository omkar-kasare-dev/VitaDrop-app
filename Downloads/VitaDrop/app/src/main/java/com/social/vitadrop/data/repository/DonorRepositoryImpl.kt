package com.social.vitadrop.data.repository


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
                    id = doc.id,
                    name = doc.getString("name") ?: "",
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