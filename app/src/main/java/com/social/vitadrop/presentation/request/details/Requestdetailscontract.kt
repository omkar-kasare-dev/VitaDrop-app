package com.social.vitadrop.presentation.request.details

import com.social.vitadrop.domain.model.RequestModel

// STATE: loading, error, or the loaded request (before: "null" meant both loading AND not found)
data class RequestDetailsState(
    val request: RequestModel? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// INTENT
sealed interface RequestDetailsIntent {
    data object Retry : RequestDetailsIntent
}