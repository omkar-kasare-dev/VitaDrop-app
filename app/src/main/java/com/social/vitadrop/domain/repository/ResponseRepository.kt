package com.social.vitadrop.domain.repository
/*
import com.social.vitadrop.domain.model.RequestModel
import kotlinx.coroutines.flow.Flow

interface ResponseRepository {
    suspend fun respondToRequest(requestId: String): Result<Unit>
    suspend fun hasAlreadyResponded(requestId: String): Result<Boolean>
    fun observeResponseCount(requestId: String): Flow<Int>
    suspend fun getRequestById(requestId: String): Result<RequestModel>
}


 */



import com.social.vitadrop.domain.model.DonorResponse
import com.social.vitadrop.domain.model.RequestModel
import kotlinx.coroutines.flow.Flow

interface ResponseRepository {

    /** "I can donate". Safe to call twice. Also works after declining or withdrawing. */
    suspend fun respondToRequest(requestId: String): Result<Unit>

    /**
     * "Not available". On a request the donor has not answered yet it records a decline;
     * after "I can donate" it records a withdrawal. Safe to call twice.
     */
    suspend fun declineRequest(requestId: String): Result<Unit>

    /** The current donor's answer to this request, or null when they have not answered. */
    suspend fun getMyResponse(requestId: String): Result<DonorResponse?>

    /** True only while the donor's answer is "I can donate". */
    suspend fun hasAlreadyResponded(requestId: String): Result<Boolean>

    fun observeResponseCount(requestId: String): Flow<Int>

    suspend fun getRequestById(requestId: String): Result<RequestModel>
}