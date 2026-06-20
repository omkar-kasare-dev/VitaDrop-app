package com.social.vitadrop.domain.usecase


import com.social.vitadrop.domain.repository.ResponseRepository

import kotlinx.coroutines.flow.Flow

class ObserveResponseCountUseCase(

    private val repository: ResponseRepository

) {

    operator fun invoke(
        requestId: String
    ): Flow<Int> {

        return repository
            .observeResponseCount(
                requestId
            )
    }
}