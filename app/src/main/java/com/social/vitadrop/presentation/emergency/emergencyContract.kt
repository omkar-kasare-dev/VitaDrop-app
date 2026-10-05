package com.social.vitadrop.presentation.emergency

// STATE: what the emergency cards need to draw
data class EmergencyState(
    val respondedRequests: Map<String, Boolean> = emptyMap(),   // requestId -> has this donor responded
    val responseCounts: Map<String, Int> = emptyMap(),          // requestId -> live number of responses
    val respondingRequestIds: Set<String> = emptySet()          // requests whose "Respond" is in progress
) {
    // Kept so existing card code that reads uiState.isLoading still compiles.
    val isLoading: Boolean get() = respondingRequestIds.isNotEmpty()
}

// INTENT: what the cards can ask the ViewModel to do
sealed interface EmergencyIntent {
    data class Respond(val requestId: String) : EmergencyIntent

    // A card appeared on screen: the ViewModel itself checks the response status
    // and starts the live response count (replaces CheckAlreadyResponded + ObserveResponseCount).
    data class RequestShown(val requestId: String) : EmergencyIntent
}

// EFFECT: one-shot messages (toast), never stored in State
sealed interface EmergencyEffect {
    data class ShowMessage(val text: String) : EmergencyEffect
}