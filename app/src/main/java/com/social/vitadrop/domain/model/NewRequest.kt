package com.social.vitadrop.domain.model


/**
 * Data a hospital user types into the "Request Blood" form.
 *
 * It deliberately has NO hospitalId, hospitalName, requestedBy or status.
 * The data layer fills those in from the logged-in hospital's profile,
 * so a user can never create a request on behalf of another hospital.
 */
data class NewRequest(
    val patientName: String,
    val bloodGroup: String,
    val unitsRequired: Int,
    val contactPerson: String,
    val contactPhone: String,
    val city: String,
    val description: String,
    val urgency: Urgency,
    val isEmergency: Boolean,
    val latitude: Double,
    val longitude: Double
)