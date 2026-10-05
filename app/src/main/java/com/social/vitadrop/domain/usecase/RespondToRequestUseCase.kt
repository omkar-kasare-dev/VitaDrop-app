package com.social.vitadrop.domain.usecase


import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.ResponseRepository
import kotlinx.coroutines.flow.Flow

/**
 * Donor taps "Respond".
 * Safe to call twice: if the donor already responded, it succeeds without writing again.
 * The current user is read in the data layer, never passed in from the UI.
 */
class RespondToRequestUseCase(
    private val repository: ResponseRepository
) {
    suspend operator fun invoke(requestId: String): Result<Unit> {
        val alreadyResponded = repository.hasAlreadyResponded(requestId)
            .getOrElse { return Result.failure(it) }

        return if (alreadyResponded) {
            Result.success(Unit)
        } else {
            repository.respondToRequest(requestId)
        }
    }
}

/** Has the current donor already responded to this request? */
class HasAlreadyRespondedUseCase(
    private val repository: ResponseRepository
) {
    suspend operator fun invoke(requestId: String): Result<Boolean> =
        repository.hasAlreadyResponded(requestId)
}

/** Live number of donors who responded to this request. */
class ObserveResponseCountUseCase(
    private val repository: ResponseRepository
) {
    operator fun invoke(requestId: String): Flow<Int> =
        repository.observeResponseCount(requestId)
}

/** Loads one request. Fails with a readable message when it does not exist or cannot be reached. */
class GetRequestByIdUseCase(
    private val repository: ResponseRepository
) {
    suspend operator fun invoke(requestId: String): Result<RequestModel> =
        repository.getRequestById(requestId)
}