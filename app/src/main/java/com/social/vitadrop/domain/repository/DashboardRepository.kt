package com.social.vitadrop.domain.repository


import com.social.vitadrop.domain.model.RequestModel

interface DashboardRepository {
    suspend fun getDonorCount(): Result<Int>
    suspend fun getHospitalCount(): Result<Int>
    suspend fun getRequestCount(): Result<Int>
    suspend fun getEmergencyRequests(): Result<List<RequestModel>>   // newest first
    suspend fun getAllRequests(): Result<List<RequestModel>>         // newest first, max 50
}