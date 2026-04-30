package com.social.vitadrop.data.repository

import com.social.vitadrop.domain.model.RequestModel


interface DashboardRepository {

    suspend fun getDonorCount(): Int
    suspend fun getHospitalCount(): Int
    suspend fun getRequestCount(): Int

    suspend fun getEmergencyRequests(): List<RequestModel>
    suspend fun getAllRequests(): List<RequestModel>
}