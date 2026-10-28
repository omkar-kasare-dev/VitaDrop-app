package com.social.vitadrop.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.social.vitadrop.data.mapper.toRequestModel
import com.social.vitadrop.data.remote.firestoreCall
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.DashboardRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DashboardRepositoryImpl : DashboardRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
    // REQUESTS ON THE DASHBOARD: a LIVE listener instead of a one-shot get()
    //
    // STEP 3:
    //  - a DONOR sees the requests that were SENT TO THEM (their uid is in notifiedDonors),
    //    emergency or not. They can answer exactly these: the Firestore rules refuse an answer
    //    to a request that was not sent to the donor.
    //  - a HOSPITAL sees the emergency requests, as before.
    //
    // Why a listener: Query.get() waits until Firestore has confirmed EVERY cached document with
    // the server. When the on-device cache holds stale data (for example left by a run against
    // the Firebase EMULATOR), that confirmation can stay open forever and get() never returns.
    // A listener shows the cached result at once and corrects it as soon as the server answers.
    // ------------------------------------------------------------------
    override fun observeEmergencyRequests(): Flow<List<RequestModel>> = callbackFlow {

        val uid = auth.currentUser?.uid

        // The donor profile is readable by its owner only, so a missing document means "not a donor"
        val isDonor = uid != null && try {
            db.collection("donors").document(uid).get().await().exists()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }

        Log.d(TAG, "[requests] listener attached (donor=$isDonor)")

        val query = if (isDonor) {
            db.collection("requests").whereArrayContains("notifiedDonors", uid!!)
        } else {
            db.collection("requests").whereEqualTo("emergency", true)
        }

        val registration = query.addSnapshotListener { snapshot, error ->

            if (error != null) {
                Log.e(
                    TAG,
                    "[requests] listener FAILED [code=${error.code}]: ${error.message}",
                    error
                )
                close(error)
                return@addSnapshotListener
            }

            if (snapshot == null) return@addSnapshotListener

            Log.d(
                TAG,
                "[requests] snapshot: ${snapshot.size()} docs, " +
                        "fromCache=${snapshot.metadata.isFromCache}"
            )

            trySend(
                snapshot.documents
                    .mapNotNull { it.toRequestOrNull() }
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }
            )
        }

        awaitClose {
            Log.d(TAG, "[requests] listener removed")
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