package com.social.vitadrop.domain.repository

import com.social.vitadrop.domain.model.RequestModel

interface RequestRepository {

    suspend fun createRequest(request: RequestModel)
}