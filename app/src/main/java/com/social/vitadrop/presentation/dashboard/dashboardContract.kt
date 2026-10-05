package com.social.vitadrop.presentation.dashboard

import com.social.vitadrop.domain.model.RequestModel

data class DonorDashboardState(
    val donorsCount: Int = 0,
    val hospitalsCount: Int = 0,
    val requestsCount: Int = 0,         // delete if always equal to requests.size
    val emergencyRequests: List<RequestModel> = emptyList(),
    val requests: List<RequestModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface DonorDashboardIntent {
    data object Load : DonorDashboardIntent      // merges LoadDashboard + LoadRequests
    data object Refresh : DonorDashboardIntent
}