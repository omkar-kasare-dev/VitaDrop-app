package com.social.vitadrop.domain.repository

import com.social.vitadrop.domain.model.RequestModel
import kotlinx.coroutines.flow.Flow

interface ResponseRepository {
    suspend fun respondToRequest(requestId: String): Result<Unit>
    suspend fun hasAlreadyResponded(requestId: String): Result<Boolean>
    fun observeResponseCount(requestId: String): Flow<Int>
    suspend fun getRequestById(requestId: String): Result<RequestModel>
}