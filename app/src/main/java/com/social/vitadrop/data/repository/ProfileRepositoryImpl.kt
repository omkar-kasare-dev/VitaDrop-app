package com.social.vitadrop.data.repository


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.UserProfile
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.repository.ProfileRepository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

class ProfileRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ProfileRepository {

    // Before: three nested callbacks (donors -> hospitals -> admin) inside the ViewModel,
    // and every failure was stored in a "message" that no screen showed.
    // Now: plain sequential code, and every failure is returned to the screen.
    override suspend fun getProfile(): Result<UserProfile> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Session expired. Please login again."))

            val donorDoc = fetch("donors", uid)
                ?: return Result.failure(IOException(NETWORK_MESSAGE))
            if (donorDoc.exists()) return Result.success(donorDoc.toDonorProfile(uid))

            val hospitalDoc = fetch("hospitals", uid)
                ?: return Result.failure(IOException(NETWORK_MESSAGE))
            if (hospitalDoc.exists()) return Result.success(hospitalDoc.toHospitalProfile(uid))

            // The old "admin" collection is not supported any more: admin accounts cannot log in.
            Result.failure(Exception("Profile not found for this account."))

        } catch (e: Exception) {
            Log.e(TAG, "getProfile failed", e)
            Result.failure(e)
        }
    }

    private suspend fun fetch(collection: String, uid: String): DocumentSnapshot? =
        withTimeoutOrNull(TIMEOUT_MS) {
            db.collection(collection).document(uid).get().await()
        }

    private fun DocumentSnapshot.toDonorProfile(uid: String) = UserProfile(
        uid = uid,
        role = UserRole.DONOR,
        fullName = getString("fullName") ?: "",
        email = getString("email") ?: "",
        phone = getString("phone") ?: "",
        city = getString("city") ?: "",
        state = getString("state") ?: "",
        address = getString("address") ?: "",
        profileImage = getString("profileImage") ?: "",
        isVerified = getBoolean("isVerified") ?: false,
        isBlocked = getBoolean("isBlocked") ?: false,
        gender = getString("gender") ?: "",
        age = getLong("age")?.toInt() ?: 0,
        bloodGroup = getString("bloodGroup") ?: "",
        weightKg = getDouble("weight") ?: 0.0,
        isAvailable = getBoolean("isAvailable") ?: false
    )

    private fun DocumentSnapshot.toHospitalProfile(uid: String) = UserProfile(
        uid = uid,
        role = UserRole.HOSPITAL,
        fullName = getString("hospitalName") ?: "",
        email = getString("email") ?: "",
        phone = getString("phone") ?: "",
        city = getString("city") ?: "",
        state = getString("state") ?: "",
        address = getString("address") ?: "",
        profileImage = getString("profileImage") ?: "",
        isVerified = getBoolean("isVerified") ?: false,
        isBlocked = getBoolean("isBlocked") ?: false,
        licenseNumber = getString("licenseNumber") ?: ""
    )

    private companion object {
        const val TAG = "PROFILE_REPO"
        const val TIMEOUT_MS = 15_000L
        const val NETWORK_MESSAGE =
            "Could not reach the server. Check your internet connection and try again."
    }
}