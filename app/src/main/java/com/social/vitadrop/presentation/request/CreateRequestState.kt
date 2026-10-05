package com.social.vitadrop.presentation.request

import com.social.vitadrop.domain.model.Urgency

// STATE: everything the form shows
data class CreateRequestState(
    val patientName: String = "",
    val bloodGroup: String = "",
    val unitsRequired: String = "",
    val contactPerson: String = "",
    val contactNumber: String = "",
    val city: String = "",
    val description: String = "",
    val urgency: Urgency = Urgency.MEDIUM,
    val isEmergency: Boolean = false,
    val latitude: String = "",
    val longitude: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class RequestField {
    PATIENT_NAME, BLOOD_GROUP, UNITS, CONTACT_PERSON, CONTACT_NUMBER, CITY, DESCRIPTION
}

// INTENT: everything the user (or the location helper) can do
sealed interface CreateRequestIntent {
    data class FieldChanged(val field: RequestField, val value: String) : CreateRequestIntent
    data class UrgencyChanged(val value: Urgency) : CreateRequestIntent
    data class EmergencyToggled(val value: Boolean) : CreateRequestIntent
    data class LocationReceived(val lat: Double, val lng: Double) : CreateRequestIntent
    data object Submit : CreateRequestIntent
}

// EFFECT: one-shot event (replaces the old isSuccess flag)
sealed interface CreateRequestEffect {
    data object RequestCreated : CreateRequestEffect
}