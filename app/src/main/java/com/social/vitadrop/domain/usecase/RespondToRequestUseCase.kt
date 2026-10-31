package com.social.vitadrop.domain.usecase

/*

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

 */

import com.social.vitadrop.domain.model.DonorResponse
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.ResponseRepository
import kotlinx.coroutines.flow.Flow

/**
 * Donor taps "I can donate".
 * Safe to call twice, and it also works after "not available": the repository decides whether
 * to create the response or change it. The current user is read in the data layer, never
 * passed in from the UI.
 */
class RespondToRequestUseCase(
    private val repository: ResponseRepository
) {
    suspend operator fun invoke(requestId: String): Result<Unit> =
        repository.respondToRequest(requestId)
}

/**
 * Donor taps "Not available" (or "I can't donate any more" after saying yes).
 * Safe to call twice.
 */
class DeclineRequestUseCase(
    private val repository: ResponseRepository
) {
    suspend operator fun invoke(requestId: String): Result<Unit> =
        repository.declineRequest(requestId)
}

/** The current donor's answer to a request (null = no answer yet), with what the hospital recorded. */
class GetMyResponseUseCase(
    private val repository: ResponseRepository
) {
    suspend operator fun invoke(requestId: String): Result<DonorResponse?> =
        repository.getMyResponse(requestId)
}

/** Has the current donor said "I can donate" to this request? */
class HasAlreadyRespondedUseCase(
    private val repository: ResponseRepository
) {
    suspend operator fun invoke(requestId: String): Result<Boolean> =
        repository.hasAlreadyResponded(requestId)
}

/** Live number of responses (only the hospital that owns the request may read them all). */
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