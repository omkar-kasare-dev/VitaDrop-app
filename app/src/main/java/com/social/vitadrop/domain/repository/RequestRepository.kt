package com.social.vitadrop.domain.repository

import com.social.vitadrop.domain.model.RequestModel
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    suspend fun createRequest(request: RequestModel)


}