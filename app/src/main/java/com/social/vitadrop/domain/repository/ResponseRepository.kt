package com.social.vitadrop.domain.repository


import com.social.vitadrop.domain.model.RequestModel
import kotlinx.coroutines.flow.Flow

interface ResponseRepository {

    suspend fun respondToRequest(
        requestId: String,
        donorId: String
    )

    suspend fun hasAlreadyResponded(
        requestId: String,
        donorId: String
    ): Boolean

    fun observeResponseCount(
        requestId: String
    ): Flow<Int>

    suspend fun getRequestById(
        requestId: String
    ): RequestModel?
}