package com.social.vitadrop.domain.usecase



import com.social.vitadrop.domain.repository.NotificationRepository

class SaveFCMTokenUseCase(
    private val repository: NotificationRepository
) {

    suspend fun saveDonorToken(
        donorId: String,
        token: String
    ) {

        repository.saveDonorToken(
            donorId,
            token
        )
    }

    suspend fun saveHospitalToken(
        hospitalId: String,
        token: String
    ) {

        repository.saveHospitalToken(
            hospitalId,
            token
        )
    }
}