package com.social.vitadrop.presentation.emergency

data class EmergencyState(
    val isLoading: Boolean = false,
    val respondedRequests: Map<String, Boolean> = emptyMap(),   // requestId -> hasResponded
    val responseCounts: Map<String, Int> = emptyMap(),          // requestId -> count
    val error: String? = null
)

sealed interface EmergencyIntent {
    data class Respond(val requestId: String) : EmergencyIntent
    data class RequestShown(val requestId: String) : EmergencyIntent  // ViewModel then checks status + observes count itself
}

sealed interface EmergencyEffect {
    data class ShowMessage(val text: String) : EmergencyEffect
}