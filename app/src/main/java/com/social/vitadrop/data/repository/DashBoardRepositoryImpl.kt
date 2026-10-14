package com.social.vitadrop.data.repository

// Updated DashBoardImplementation.


import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.social.vitadrop.data.mapper.toRequestModel
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.DashboardRepository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

class DashboardRepositoryImpl : DashboardRepository {

    private val db = FirebaseFirestore.getInstance()

    // ------------------------------------------------------------------
    // COUNTS
    // Before: every donor / hospital / request document was DOWNLOADED just to count it.
    // Now: Firestore counts on the server and sends back one number.
    // (needs firebase-firestore 24.4.0+; aggregate queries need a server connection)
    // ------------------------------------------------------------------
    override suspend fun getDonorCount(): Result<Int> = count("donors")

    override suspend fun getHospitalCount(): Result<Int> = count("hospitals")

    override suspend fun getRequestCount(): Result<Int> = count("requests")

    // ------------------------------------------------------------------
    // REQUEST LISTS (newest first)
    // ------------------------------------------------------------------
    override suspend fun getEmergencyRequests(): Result<List<RequestModel>> =
        loadRequests(
            // Equality-only query: no composite index needed. Sorted below, in memory.
            db.collection("requests").whereEqualTo("emergency", true)
        )

    override suspend fun getAllRequests(): Result<List<RequestModel>> =
        loadRequests(
            // Single-field ordering + limit: no composite index needed, and no "download everything".
            db.collection("requests")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(MAX_REQUESTS)
        )

    // ------------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------------
    private suspend fun count(collection: String): Result<Int> {
        return try {
            val snapshot = withTimeoutOrNull(TIMEOUT_MS) {
                db.collection(collection).count().get(AggregateSource.SERVER).await()
            }

            if (snapshot == null) {
                Result.failure<Int>(IOException(NETWORK_MESSAGE))
            } else {
                Result.success(snapshot.count.toInt())
            }

        } catch (e: Exception) {
            Log.e(TAG, "Count failed for $collection", e)
            Result.failure(e)
        }
    }

    private suspend fun loadRequests(query: Query): Result<List<RequestModel>> {
        return try {
            val snapshot = withTimeoutOrNull(TIMEOUT_MS) {
                query.get().await()
            }

            if (snapshot == null) {
                Result.failure<List<RequestModel>>(IOException(NETWORK_MESSAGE))
            } else {
                val requests = snapshot.documents
                    .mapNotNull { doc ->
                        try {
                            doc.toRequestModel()
                        } catch (e: Exception) {
                            // One bad document must not break the whole list.
                            // Only the document id is logged (never patient data).
                            Log.e(TAG, "Could not parse request ${doc.id}", e)
                            null
                        }
                    }
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }

                Result.success(requests)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Loading requests failed", e)
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "DASHBOARD_REPO"
        const val TIMEOUT_MS = 15_000L
        const val MAX_REQUESTS = 50L
        const val NETWORK_MESSAGE =
            "Could not reach the server. Check your internet connection and try again."
    }
}