package com.social.vitadrop.domain.repository




interface NotificationRepository {

    suspend fun getFCMToken(): String

    suspend fun saveDonorToken(
        donorId: String,
        token: String
    )

    suspend fun saveHospitalToken(
        hospitalId: String,
        token: String
    )
}