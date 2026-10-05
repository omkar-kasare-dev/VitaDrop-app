package com.social.vitadrop.domain.repository

import com.social.vitadrop.domain.model.NewRequest

interface RequestRepository {
    suspend fun createRequest(request: NewRequest): Result<String>
}