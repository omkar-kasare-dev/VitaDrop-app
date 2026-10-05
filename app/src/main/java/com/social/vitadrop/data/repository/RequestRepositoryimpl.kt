package com.social.vitadrop.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.NewRequest
import com.social.vitadrop.domain.repository.RequestRepository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

class RequestRepositoryImpl : RequestRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun createRequest(request: NewRequest): Result<String> {
        return try {

            // 1. WHO IS CREATING THE REQUEST?
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Session expired. Please login again."))

            // Only a hospital account may create a request.
            // Hospital name / id come from the profile, never from the form.
            val hospital = withTimeoutOrNull(TIMEOUT_MS) {
                db.collection("hospitals").document(uid).get().await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            if (!hospital.exists()) {
                return Result.failure(Exception("Only hospital accounts can create blood requests."))
            }
            if (hospital.getBoolean("isBlocked") == true) {
                return Result.failure(Exception("This account has been blocked. Please contact support."))
            }

            val hospitalName = hospital.getString("hospitalName").orEmpty()

            // 2. BUILD THE DOCUMENT (same field names as before, so other screens keep working)
            val ref = db.collection("requests").document()

            val data = hashMapOf<String, Any?>(
                // REQUEST INFO
                "requestId" to ref.id,
                "requestedBy" to "hospital",
                "hospitalId" to uid,

                // PATIENT / BLOOD INFO
                "patientName" to request.patientName,
                "bloodGroup" to request.bloodGroup,
                "unitsRequired" to request.unitsRequired.toLong(),

                // CONTACT INFO
                "contactPerson" to request.contactPerson,
                "contactPhone" to request.contactPhone,

                // HOSPITAL INFO
                "hospitalName" to hospitalName,
                "city" to request.city,

                // LOCATION
                "location" to mapOf(
                    "latitude" to request.latitude,
                    "longitude" to request.longitude
                ),

                // REQUEST DETAILS
                "urgency" to request.urgency.key,
                "status" to "pending",
                "description" to request.description,

                // DONOR TRACKING
                "acceptedDonors" to emptyList<String>(),
                "rejectedDonors" to emptyList<String>(),
                "completedBy" to emptyList<String>(),

                // NOTIFICATION TRACKING
                "notificationRadius" to 5,
                "acceptedBy" to "",
                "notifiedDonors" to emptyList<String>(),
                "notificationStartedAt" to FieldValue.serverTimestamp(),

                // EMERGENCY
                "emergency" to request.isEmergency,

                // SYSTEM
                "createdBy" to uid,
                "createdAt" to FieldValue.serverTimestamp()
            )

            // 3. SAVE (timeout, so an offline device never hangs forever)
            val saved = withTimeoutOrNull(TIMEOUT_MS) {
                ref.set(data).await()
                true
            }

            if (saved == null) {
                Result.failure(IOException(NETWORK_MESSAGE))
            } else {
                Log.d("REQUEST", "Request created: ${ref.id}")
                Result.success(ref.id)
            }

        } catch (e: Exception) {
            Log.e("REQUEST", "Request creation failed", e)
            Result.failure(e)
        }
    }

    private companion object {
        const val TIMEOUT_MS = 15_000L
        const val NETWORK_MESSAGE =
            "Could not reach the server. Check your internet connection and try again."
    }
}