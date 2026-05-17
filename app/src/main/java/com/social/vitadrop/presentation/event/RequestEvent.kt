package com.social.vitadrop.presentation.event
/*
sealed class RequestEvent {

    //Patient Info
    data class OnPatientNameChange(val value: String) : RequestEvent()
    data class OnBloodGroupChange(val value: String) : RequestEvent()

    // Hospital Info
    data class OnHospitalNameChange(val value: String) : RequestEvent()
    data class OnCityChange(val value: String) : RequestEvent()

    //Contact Info
    data class OnContactNumberChange(val value: String) : RequestEvent()

    // Request Details
    data class OnUnitsChange(val value: String) : RequestEvent()
    data class OnRequestTypeChange(val value: String) : RequestEvent()

    // Emergency Toggle
    data class OnEmergencyToggle(val value: Boolean) : RequestEvent()

    object LoadEmergencyRequests : RequestEvent()

    //  Submit Action
    object SubmitRequest : RequestEvent()
}


 */
// Modified Request Event.


sealed class RequestEvent {

    // =========================
    // PATIENT INFO
    // =========================

    data class OnPatientNameChange(
        val value: String
    ) : RequestEvent()

    data class OnBloodGroupChange(
        val value: String
    ) : RequestEvent()

    data class OnUnitsChange(
        val value: String
    ) : RequestEvent()


    // =========================
    // HOSPITAL INFO
    // =========================

    data class OnHospitalNameChange(
        val value: String
    ) : RequestEvent()

    data class OnHospitalIdChange(
        val value: String
    ) : RequestEvent()

    data class OnCityChange(
        val value: String
    ) : RequestEvent()


    // =========================
    // CONTACT INFO
    // =========================

    data class OnContactPersonChange(
        val value: String
    ) : RequestEvent()

    data class OnContactNumberChange(
        val value: String
    ) : RequestEvent()


    // =========================
    // REQUEST DETAILS
    // =========================

    data class OnRequestTypeChange(
        val value: String
    ) : RequestEvent()

    data class OnUrgencyChange(
        val value: String
    ) : RequestEvent()

    data class OnStatusChange(
        val value: String
    ) : RequestEvent()

    data class OnDescriptionChange(
        val value: String
    ) : RequestEvent()


    // =========================
    // LOCATION
    // =========================

    data class OnLatitudeChange(
        val value: String
    ) : RequestEvent()

    data class OnLongitudeChange(
        val value: String
    ) : RequestEvent()


    // =========================
    // EMERGENCY
    // =========================

    data class OnEmergencyToggle(
        val value: Boolean
    ) : RequestEvent()


    // =========================
    // REQUEST MANAGEMENT
    // =========================

    object LoadEmergencyRequests : RequestEvent()

    object SubmitRequest : RequestEvent()

    object ClearForm : RequestEvent()
}