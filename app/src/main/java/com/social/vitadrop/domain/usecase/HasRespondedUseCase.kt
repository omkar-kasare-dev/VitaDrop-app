package com.social.vitadrop.domain.usecase



import com.social.vitadrop.domain.repository.ResponseRepository

class HasRespondedUseCase(

    private val repository: ResponseRepository

) {

    suspend operator fun invoke(
        requestId: String,
        donorId: String
    ): Boolean {

        return repository
            .hasAlreadyResponded(
                requestId = requestId,
                donorId = donorId
            )
    }
}