package com.social.vitadrop.data.repository
/*
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.data.mapper.toRequestModel
import com.social.vitadrop.domain.model.DonorResponse
import com.social.vitadrop.domain.model.RequestModel
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