package com.social.vitadrop.data.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.data.remote.FirebaseMessagingManager
import com.social.vitadrop.domain.repository.NotificationRepository
import kotlinx.coroutines.tasks.await

class NotificationRepositoryImpl :
    NotificationRepository {

    private val db =
        FirebaseFirestore.getInstance()

    private val messagingManager =
        FirebaseMessagingManager()

    /**
     * =====================================================
     * GET FCM TOKEN
     * =====================================================
     */
    override suspend fun getFCMToken(): String {

        return messagingManager.getToken()
    }

    /**
     * =====================================================
     * SAVE DONOR TOKEN
     * =====================================================
     */
    override suspend fun saveDonorToken(
        donorId: String,
        token: String
    ) {

        db.collection("donors")
            .document(donorId)
            .update(
                mapOf(
                    "fcmToken" to token
                )
            )
            .await()
    }

    /**
     * =====================================================
     * SAVE HOSPITAL TOKEN
     * =====================================================
     */
    override suspend fun saveHospitalToken(
        hospitalId: String,
        token: String
    ) {

        db.collection("hospitals")
            .document(hospitalId)
            .update(
                mapOf(
                    "fcmToken" to token
                )
            )
            .await()
    }
}