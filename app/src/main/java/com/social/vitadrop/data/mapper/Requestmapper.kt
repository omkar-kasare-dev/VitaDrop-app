package com.social.vitadrop.data.mapper



import com.google.firebase.firestore.DocumentSnapshot
import com.social.vitadrop.domain.model.RequestModel

/**
 * Safe Firestore document -> RequestModel conversion.
 * - never throws on wrong field types (location, units, lists)
 * - same field names as the document written by RequestRepositoryImpl
 */
internal fun DocumentSnapshot.toRequestModel(): RequestModel {

    val location = get("location") as? Map<*, *>
    val latitude = (location?.get("latitude") as? Number)?.toDouble() ?: 0.0
    val longitude = (location?.get("longitude") as? Number)?.toDouble() ?: 0.0

    // unitsRequired may be stored as a number OR (old documents) as text
    val unitsRequired: Long = when (val value = get("unitsRequired")) {
        is Number -> value.toLong()
        is String -> value.toLongOrNull() ?: 0L
        else -> 0L
    }

    return RequestModel(
        // REQUEST INFO
        requestId = getString("requestId") ?: id,
        requestedBy = getString("requestedBy") ?: "",
        hospitalId = getString("hospitalId") ?: "",

        // PATIENT / BLOOD INFO
        patientName = getString("patientName") ?: "",
        bloodGroup = getString("bloodGroup") ?: "",
        unitsRequired = unitsRequired,

        // CONTACT INFO
        contactPerson = getString("contactPerson") ?: "",
        contactPhone = getString("contactPhone") ?: "",

        // HOSPITAL INFO
        hospitalName = getString("hospitalName") ?: "",
        city = getString("city") ?: "",

        // LOCATION
        location = mapOf(
            "latitude" to latitude,
            "longitude" to longitude
        ),

        // REQUEST DETAILS
        urgency = getString("urgency") ?: "medium",
        status = getString("status") ?: "pending",
        description = getString("description") ?: "",

        // DONOR TRACKING
        acceptedDonors = stringList("acceptedDonors"),
        rejectedDonors = stringList("rejectedDonors"),
        completedBy = stringList("completedBy"),

        // NOTIFICATION TRACKING
        notificationRadius = getLong("notificationRadius")?.toInt() ?: 5,
        acceptedBy = getString("acceptedBy") ?: "",
        notifiedDonors = stringList("notifiedDonors"),
        notificationStartedAt = getTimestamp("notificationStartedAt"),

        // EMERGENCY
        emergency = getBoolean("emergency") ?: false,

        // TIMESTAMP
        createdAt = getTimestamp("createdAt")
    )
}

// Safe replacement for "as? List<String>" (an unchecked cast)
private fun DocumentSnapshot.stringList(field: String): List<String> =
    (get(field) as? List<*>)?.filterIsInstance<String>().orEmpty()