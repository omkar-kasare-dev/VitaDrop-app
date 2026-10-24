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
                // STEP 1: hospitals read the reduced donorDirectory (name, blood group, city,
                // state, gender, availability). The full donors collection is private.
                db.collection("donorDirectory").get().await()
            }

            if (snapshot == null) {
                Result.failure<List<DonorModel>>(
                    IOException("Could not reach the server. Check your internet connection and try again.")
                )
            } else {
                Result.success(snapshot.documents.map { it.toDonorModel() })
            }

        } catch (e: Exception) {
            // The error travels up so the screen can show it with a Retry button.
            Log.e("DONOR_REPO", "Failed to load donors", e)
            Result.failure(e)
        }
    }

    // The directory document id IS the donor's uid. Fields that are not in the directory
    // (phone, age, weight, location, token...) keep the DonorModel defaults.
    private fun DocumentSnapshot.toDonorModel(): DonorModel =
        DonorModel(
            uid = id,
            fullName = getString("fullName") ?: "",
            bloodGroup = getString("bloodGroup") ?: "",
            city = getString("city") ?: "",
            state = getString("state") ?: "",
            gender = getString("gender") ?: "",
            isAvailable = getBoolean("isAvailable") ?: true
        )

    private companion object {
        const val TIMEOUT_MS = 15_000L
    }
}