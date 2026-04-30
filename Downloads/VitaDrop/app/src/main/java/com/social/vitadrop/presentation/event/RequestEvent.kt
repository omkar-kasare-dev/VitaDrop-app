package com.social.vitadrop.presentation.event

sealed class RequestEvent {

    // 👤 Patient Info
    data class OnPatientNameChange(val value: String) : RequestEvent()
    data class OnBloodGroupChange(val value: String) : RequestEvent()

    // 🏥 Hospital Info
    data class OnHospitalNameChange(val value: String) : RequestEvent()
    data class OnCityChange(val value: String) : RequestEvent()

    // 📞 Contact Info
    data class OnContactNumberChange(val value: String) : RequestEvent()

    // 🩸 Request Details
    data class OnUnitsChange(val value: String) : RequestEvent()
    data class OnRequestTypeChange(val value: String) : RequestEvent()

    // 🚨 Emergency Toggle
    data class OnEmergencyToggle(val value: Boolean) : RequestEvent()

    object LoadEmergencyRequests : RequestEvent()

    // 🚀 Submit Action
    object SubmitRequest : RequestEvent()
}