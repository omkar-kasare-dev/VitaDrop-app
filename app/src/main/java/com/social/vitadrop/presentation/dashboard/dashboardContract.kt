package com.social.vitadrop.presentation.dashboard

import com.social.vitadrop.domain.model.RequestModel

// STATE: everything the dashboard draws
data class DonorDashboardState(
    val donorsCount: Int = 0,
    val hospitalsCount: Int = 0,
    val requestsCount: Int = 0,                                  // total requests (a server-side count)
    val emergencyRequests: List<RequestModel> = emptyList(),     // open emergency requests, newest first
    val isLoading: Boolean = true,
    val error: String? = null
    // "requests" was removed: the Blood Requests screen now has its own ViewModel.
)

// INTENT
sealed interface DonorDashboardIntent {
    // Used when the screen opens, when the user comes back to it, and by the Retry button
    data object Refresh : DonorDashboardIntent
}