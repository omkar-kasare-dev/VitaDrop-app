package com.social.vitadrop.data.repository



import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.DonorResponse
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.ResponseRepository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ResponseRepositoryImpl(

    private val firestore: FirebaseFirestore

) : ResponseRepository {

    /**
     * Save donor response
     */
    override suspend fun respondToRequest(
        requestId: String,
        donorId: String
    ) {

        val response = DonorResponse(
            donorId = donorId,
            requestId = requestId
        )

        firestore
            .collection("requests")
            .document(requestId)
            .collection("responses")
            .document(donorId)
            .set(response)
            .await()
    }

    /**
     * Prevent duplicate response
     */
    override suspend fun hasAlreadyResponded(
        requestId: String,
        donorId: String
    ): Boolean {

        val snapshot = firestore
            .collection("requests")
            .document(requestId)
            .collection("responses")
            .document(donorId)
            .get()
            .await()

        return snapshot.exists()
    }

    /**
     * Real-time response count
     */
    override fun observeResponseCount(
        requestId: String
    ): Flow<Int> = callbackFlow {

        val listener = firestore
            .collection("requests")
            .document(requestId)
            .collection("responses")
            .addSnapshotListener { snapshot, _ ->

                val count =
                    snapshot?.documents?.size ?: 0

                trySend(count)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun getRequestById(
        requestId: String
    ): RequestModel? {

        return firestore
            .collection("requests")
            .document(requestId)
            .get()
            .await()
            .toObject(RequestModel::class.java)
    }
}