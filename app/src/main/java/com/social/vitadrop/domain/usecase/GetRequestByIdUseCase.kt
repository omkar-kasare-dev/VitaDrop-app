package com.social.vitadrop.domain.usecase



import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.ResponseRepository

class GetRequestByIdUseCase(

    private val repository: ResponseRepository

) {

    suspend operator fun invoke(
        requestId: String
    ): RequestModel? {

        return repository
            .getRequestById(
                requestId
            )
    }
}