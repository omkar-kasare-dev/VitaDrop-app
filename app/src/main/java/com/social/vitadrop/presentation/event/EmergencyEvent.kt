package com.social.vitadrop.presentation.event



sealed class EmergencyEvent {

    data class RespondToRequest(
        val requestId: String
    ) : EmergencyEvent()

    data class ObserveResponseCount(
        val requestId: String
    ) : EmergencyEvent()

    data class CheckAlreadyResponded(
        val requestId: String
    ) : EmergencyEvent()
}
/*
sealed class EmergencyEvent {

    data class RespondToRequest(
        val requestId: String
    ) : EmergencyEvent()

    data class CheckAlreadyResponded(
        val requestId: String
    ) : EmergencyEvent()

    data class ObserveResponseCount(
        val requestId: String
    ) : EmergencyEvent()
}

 */