package com.social.vitadrop.state

import com.social.vitadrop.domain.model.RequestModel


data class DonorDashboardState(
    val donorsCount: Int = 0,
    val hospitalsCount: Int = 0,
    val requestsCount: Int = 0,
    val emergencyRequests: List<RequestModel> = emptyList(),
    val isLoading: Boolean = false,
    val requests: List<RequestModel> = emptyList(),
    val error: String? = null
)