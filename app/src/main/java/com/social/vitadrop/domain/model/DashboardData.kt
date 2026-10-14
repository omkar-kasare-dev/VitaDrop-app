package com.social.vitadrop.domain.model

data class DashboardData(
    val donorsCount: Int,
    val hospitalsCount: Int,
    val requestsCount: Int,
    val emergencyRequests: List<RequestModel>
)