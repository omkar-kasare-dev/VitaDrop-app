package com.social.vitadrop.domain.usecase

import com.social.vitadrop.domain.repository.ResponseRepository

class RespondToRequestUseCase(

    private val repository: ResponseRepository

) {

    suspend operator fun invoke(
        requestId: String,
        donorId: String
    ) {

        repository.respondToRequest(
            requestId = requestId,
            donorId = donorId
        )
    }
}