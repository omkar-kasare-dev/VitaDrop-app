package com.social.vitadrop.domain.repository

import com.social.vitadrop.domain.model.RequestModel
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    suspend fun getDonorCount(): Result<Int>
    suspend fun getHospitalCount(): Result<Int>
    suspend fun getRequestCount(): Result<Int>
    fun observeEmergencyRequests(): Flow<List<RequestModel>>   // live, newest first
    suspend fun getAllRequests(): Result<List<RequestModel>>
}