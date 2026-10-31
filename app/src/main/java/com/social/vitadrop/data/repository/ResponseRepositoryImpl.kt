package com.social.vitadrop.data.repository
/*
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.data.mapper.toRequestModel
import com.social.vitadrop.domain.model.DonorResponse
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.model.RequestStatus
import com.social.vitadrop.domain.repository.ResponseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

class ResponseRepositoryImpl(
    private val firestore: FirebaseFirestore,
    // default value: NavGraph can keep calling ResponseRepositoryImpl(FirebaseFirestore.getInstance())
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ResponseRepository {

    /**
     * Save the donor's response.
     * The donor id is the logged-in user, never a value sent from the UI.
     */
    override suspend fun respondToRequest(requestId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception(SESSION_MESSAGE))

            // Only a donor (not blocked) may respond
            val donor = withTimeoutOrNull(TIMEOUT_MS) {
                firestore.collection("donors").document(uid).get().await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            if (!donor.exists()) {
                return Result.failure(Exception("Only donor accounts can respond to blood requests."))
            }
            if (donor.getBoolean("isBlocked") == true) {
                return Result.failure(Exception("This account has been blocked. Please contact support."))
            }

            // The request must still exist
            val requestRef = firestore.collection("requests").document(requestId)

            val request = withTimeoutOrNull(TIMEOUT_MS) {
                requestRef.get().await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            if (!request.exists()) {
                return Result.failure(Exception("This request no longer exists."))
            }

            // STEP 3: the same two rules the Firestore rules enforce, with a readable message
            if (!RequestStatus.fromKey(request.getString("status")).isOpen) {
                return Result.failure(Exception("This request is closed, so it no longer needs donors."))
            }

            val alerted = (request.get("notifiedDonors") as? List<*>)?.contains(uid) == true
            if (!alerted) {
                return Result.failure(Exception("This request was not sent to you, so you cannot respond to it."))
            }

            // Same document shape as before: requests/{id}/responses/{donorId}
            val saved = withTimeoutOrNull(TIMEOUT_MS) {
                requestRef
                    .collection("responses")
                    .document(uid)
                    .set(DonorResponse(donorId = uid, requestId = requestId))
                    .await()
                true
            }

            if (saved == null) Result.failure(IOException(NETWORK_MESSAGE)) else Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "respondToRequest failed", e)
            Result.failure(e)
        }
    }

    /** Prevent duplicate responses. */
    override suspend fun hasAlreadyResponded(requestId: String): Result<Boolean> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception(SESSION_MESSAGE))

            val snapshot = withTimeoutOrNull(TIMEOUT_MS) {
                firestore
                    .collection("requests")
                    .document(requestId)
                    .collection("responses")
                    .document(uid)
                    .get()
                    .await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            Result.success(snapshot.exists())

        } catch (e: Exception) {
            Log.e(TAG, "hasAlreadyResponded failed", e)
            Result.failure(e)
        }
    }

    /**
     * Real-time response count.
     * STEP 1 NOTE: only the hospital that owns the request may read the whole responses
     * collection (Firestore rules). Donor screens use request.responseCount instead.
     * Kept so the interface does not change.
     */
    override fun observeResponseCount(requestId: String): Flow<Int> = callbackFlow {

        val listener = firestore
            .collection("requests")
            .document(requestId)
            .collection("responses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.size())
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getRequestById(requestId: String): Result<RequestModel> {
        return try {
            val document = withTimeoutOrNull(TIMEOUT_MS) {
                firestore.collection("requests").document(requestId).get().await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            if (!document.exists()) {
                Result.failure(Exception("Request not found."))
            } else {
                // same mapper as the dashboard, so every screen reads a request the same way
                Result.success(document.toRequestModel())
            }

        } catch (e: Exception) {
            Log.e(TAG, "getRequestById failed", e)
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "RESPONSE_REPO"
        const val TIMEOUT_MS = 15_000L
        const val SESSION_MESSAGE = "Session expired. Please login again."
        const val NETWORK_MESSAGE =
            "Could not reach the server. Check your internet connection and try again."
    }
}

 */


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.data.mapper.toRequestModel
import com.social.vitadrop.domain.model.AttendanceStatus
import com.social.vitadrop.domain.model.DonationStatus
import com.social.vitadrop.domain.model.DonorResponse
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.model.RequestStatus
import com.social.vitadrop.domain.model.ResponseStatus
import com.social.vitadrop.domain.repository.ResponseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

class ResponseRepositoryImpl(
    private val firestore: FirebaseFirestore,
    // default value: NavGraph can keep calling ResponseRepositoryImpl(FirebaseFirestore.getInstance())
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ResponseRepository {

    override suspend fun respondToRequest(requestId: String): Result<Unit> =
        saveAnswer(requestId, ResponseStatus.ACCEPTED)

    override suspend fun declineRequest(requestId: String): Result<Unit> =
        saveAnswer(requestId, ResponseStatus.DECLINED)

    /**
     * Records the donor's answer in requests/{id}/responses/{donorId}.
     * The donor id is the logged-in user, never a value sent from the UI, and the time is the
     * SERVER's (a phone clock can be wrong). The Firestore rules check the same things again.
     *
     * wanted = ACCEPTED ("I can donate") or DECLINED ("not available").
     */
    private suspend fun saveAnswer(requestId: String, wanted: ResponseStatus): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception(SESSION_MESSAGE))

            // Only a donor (not blocked) may respond
            val donor = withTimeoutOrNull(TIMEOUT_MS) {
                firestore.collection("donors").document(uid).get().await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            if (!donor.exists()) {
                return Result.failure(Exception("Only donor accounts can respond to blood requests."))
            }
            if (donor.getBoolean("isBlocked") == true) {
                return Result.failure(Exception("This account has been blocked. Please contact support."))
            }

            // The request must exist, still be open, and have been sent to this donor
            val requestRef = firestore.collection("requests").document(requestId)

            val request = withTimeoutOrNull(TIMEOUT_MS) {
                requestRef.get().await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            if (!request.exists()) {
                return Result.failure(Exception("This request no longer exists."))
            }
            if (!RequestStatus.fromKey(request.getString("status")).isOpen) {
                return Result.failure(Exception("This request is closed, so it no longer needs donors."))
            }
            val alerted = (request.get("notifiedDonors") as? List<*>)?.contains(uid) == true
            if (!alerted) {
                return Result.failure(Exception("This request was not sent to you, so you cannot respond to it."))
            }

            val responseRef = requestRef.collection("responses").document(uid)

            val existing = withTimeoutOrNull(TIMEOUT_MS) {
                responseRef.get().await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            val saved: Boolean? = if (!existing.exists()) {

                // First answer
                withTimeoutOrNull(TIMEOUT_MS) {
                    responseRef.set(
                        mapOf(
                            "donorId" to uid,
                            "requestId" to requestId,
                            "responseStatus" to wanted.key,
                            "attendanceStatus" to AttendanceStatus.NOT_ARRIVED.key,
                            "donationStatus" to DonationStatus.NOT_STARTED.key,
                            "respondedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                    true
                }

            } else {

                // The donor changes their mind
                val current = existing.toDonorResponse()

                if (!current.canChange) {
                    return Result.failure(
                        Exception("The hospital has already recorded your visit, so this can no longer be changed.")
                    )
                }

                val target = when (wanted) {
                    ResponseStatus.ACCEPTED -> ResponseStatus.ACCEPTED
                    // "not available" after "I can donate" is a withdrawal; otherwise it stays as it is
                    else -> if (current.responseStatus == ResponseStatus.ACCEPTED) ResponseStatus.WITHDRAWN else current.responseStatus
                }

                if (current.responseStatus == target) {
                    true                                  // nothing to change: safe to call twice
                } else {
                    withTimeoutOrNull(TIMEOUT_MS) {
                        responseRef.update(
                            mapOf(
                                "responseStatus" to target.key,
                                "respondedAt" to FieldValue.serverTimestamp()
                            )
                        ).await()
                        true
                    }
                }
            }

            if (saved == null) Result.failure(IOException(NETWORK_MESSAGE)) else Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "saveAnswer failed", e)
            Result.failure(e)
        }
    }

    override suspend fun getMyResponse(requestId: String): Result<DonorResponse?> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception(SESSION_MESSAGE))

            val snapshot = withTimeoutOrNull(TIMEOUT_MS) {
                firestore
                    .collection("requests")
                    .document(requestId)
                    .collection("responses")
                    .document(uid)
                    .get()
                    .await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            Result.success(if (snapshot.exists()) snapshot.toDonorResponse() else null)

        } catch (e: Exception) {
            Log.e(TAG, "getMyResponse failed", e)
            Result.failure(e)
        }
    }

    override suspend fun hasAlreadyResponded(requestId: String): Result<Boolean> =
        getMyResponse(requestId).map { it?.responseStatus == ResponseStatus.ACCEPTED }

    /**
     * Real-time response count.
     * Only the hospital that owns the request may read the whole responses collection
     * (Firestore rules). Donor screens use request.responseCount instead. Kept so the
     * interface does not change.
     */
    override fun observeResponseCount(requestId: String): Flow<Int> = callbackFlow {

        val listener = firestore
            .collection("requests")
            .document(requestId)
            .collection("responses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.size())
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getRequestById(requestId: String): Result<RequestModel> {
        return try {
            val document = withTimeoutOrNull(TIMEOUT_MS) {
                firestore.collection("requests").document(requestId).get().await()
            } ?: return Result.failure(IOException(NETWORK_MESSAGE))

            if (!document.exists()) {
                Result.failure(Exception("Request not found."))
            } else {
                // same mapper as the dashboard, so every screen reads a request the same way
                Result.success(document.toRequestModel())
            }

        } catch (e: Exception) {
            Log.e(TAG, "getRequestById failed", e)
            Result.failure(e)
        }
    }

    private fun DocumentSnapshot.toDonorResponse() = DonorResponse(
        donorId = getString("donorId") ?: id,
        requestId = getString("requestId") ?: "",
        responseStatus = ResponseStatus.fromKey(getString("responseStatus")),
        attendanceStatus = AttendanceStatus.fromKey(getString("attendanceStatus")),
        donationStatus = DonationStatus.fromKey(getString("donationStatus"))
    )

    private companion object {
        const val TAG = "RESPONSE_REPO"
        const val TIMEOUT_MS = 15_000L
        const val SESSION_MESSAGE = "Session expired. Please login again."
        const val NETWORK_MESSAGE =
            "Could not reach the server. Check your internet connection and try again."
    }
}