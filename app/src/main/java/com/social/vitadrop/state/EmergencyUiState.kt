package com.social.vitadrop.state


/*
data class EmergencyUiState(

    val isLoading: Boolean = false,

    val hasResponded: Boolean = false,

    val responseCount: Int = 0,

    val error: String? = null
)

 */



data class EmergencyUiState(

    val isLoading: Boolean = false,

    // requestId -> hasResponded
    val respondedRequests: Map<String, Boolean> =
        emptyMap(),

    // requestId -> responseCount
    val responseCounts: Map<String, Int> =
        emptyMap(),

    val error: String? = null
)