package com.social.vitadrop.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.social.vitadrop.data.mapper.toRequestModel
import com.social.vitadrop.data.remote.firestoreCall
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.DashboardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DashboardRepositoryImpl : DashboardRepository {

    private val db = FirebaseFirestore.getInstance()

    // ------------------------------------------------------------------
    // COUNTS: STEP 1. The numbers are read from ONE small document, stats/global,
    // kept up to date by Cloud Functions. Before, the app counted the donors and hospitals
    // collections itself, which needed permission to list every donor and hospital.
    // ------------------------------------------------------------------
    override suspend fun getDonorCount(): Result<Int> = statCount("donorsCount")

    override suspend fun getHospitalCount(): Result<Int> = statCount("hospitalsCount")

    override suspend fun getRequestCount(): Result<Int> = statCount("requestsCount")

    private suspend fun statCount(field: String): Result<Int> =
        firestoreCall(TAG, "stats/global.$field") {
            db.collection("stats")
                .document("global")
                .get()
                .await()
                .getLong(field)
                ?.toInt()
                ?: 0
        }

    // ------------------------------------------------------------------
    // EMERGENCY REQUESTS: a LIVE listener instead of a one-shot get()
    //
    // Why: Query.get() waits until Firestore has confirmed EVERY cached document with the
    // server. When the on-device cache holds stale data (for example left by a run against the
    // Firebase EMULATOR), that confirmation can stay open forever and get() never returns:
    // the dashboard then fails with a timeout. A listener shows the cached result at once and
    // corrects it as soon as the server answers, so it can never hang the screen.
    // As a bonus, a new emergency request appears on the dashboard by itself.
    // ------------------------------------------------------------------
    override fun observeEmergencyRequests(): Flow<List<RequestModel>> = callbackFlow {

        Log.d(TAG, "[emergency requests] listener attached")

        val registration = db.collection("requests")
            .whereEqualTo("emergency", true)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e(
                        TAG,
                        "[emergency requests] listener FAILED [code=${error.code}]: ${error.message}",
                        error
                    )
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                Log.d(
                    TAG,
                    "[emergency requests] snapshot: ${snapshot.size()} docs, " +
                            "fromCache=${snapshot.metadata.isFromCache}"
                )

                trySend(
                    snapshot.documents
                        .mapNotNull { it.toRequestOrNull() }
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                )
            }

        awaitClose {
            Log.d(TAG, "[emergency requests] listener removed")
            registration.remove()
        }
    }

    // ------------------------------------------------------------------
    // ALL REQUESTS (newest first, max 50) for the "Blood Requests" list
    // ------------------------------------------------------------------
    override suspend fun getAllRequests(): Result<List<RequestModel>> =
        firestoreCall(TAG, "all requests (latest $MAX_REQUESTS)") {
            db.collection("requests")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(MAX_REQUESTS)
                .get()
                .await()
                .documents
                .mapNotNull { it.toRequestOrNull() }
                .sortedByDescending { it.createdAt?.seconds ?: 0L }
        }

    // One bad document must not break the whole list.
    // Only the document id is logged (never patient data).
    private fun DocumentSnapshot.toRequestOrNull(): RequestModel? =
        try {
            toRequestModel()
        } catch (e: Exception) {
            Log.e(TAG, "Could not parse request $id", e)
            null
        }

    private companion object {
        const val TAG = "DASHBOARD_REPO"
        const val MAX_REQUESTS = 50L
    }
}