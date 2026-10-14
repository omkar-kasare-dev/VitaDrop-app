package com.social.vitadrop.presentation.request.list



import com.social.vitadrop.domain.model.RequestModel

// STATE
data class RequestListState(
    val requests: List<RequestModel> = emptyList(),   // open requests, newest first
    val isLoading: Boolean = true,
    val error: String? = null
)

// INTENT
sealed interface RequestListIntent {
    data object Refresh : RequestListIntent           // also used by the Retry button
}