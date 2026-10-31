package com.social.vitadrop.domain.model

/**
 * A donor's answer to a request, kept in three separate facts:
 *  - ResponseStatus    set by the DONOR: "I can donate", "not available", or "I changed my mind"
 *  - AttendanceStatus  set by the HOSPITAL: did the donor come
 *  - DonationStatus    set by the HOSPITAL: was a donation collected or deferred
 *
 * "Accepted" only means willing. It does not mean eligible or that blood was given.
 * The keys must match the Cloud Functions and the Firestore rules.
 * A response written before Step 4 has none of these fields: it reads as accepted / not arrived.
 */
enum class ResponseStatus(val key: String) {
    ACCEPTED("accepted"),
    DECLINED("declined"),
    WITHDRAWN("withdrawn");

    companion object {
        fun fromKey(key: String?): ResponseStatus =
            entries.firstOrNull { it.key == key?.trim()?.lowercase() } ?: ACCEPTED
    }
}

enum class AttendanceStatus(val key: String) {
    NOT_ARRIVED("not_arrived"),
    ARRIVED("arrived"),
    NO_SHOW("no_show");

    companion object {
        fun fromKey(key: String?): AttendanceStatus =
            entries.firstOrNull { it.key == key?.trim()?.lowercase() } ?: NOT_ARRIVED
    }
}

enum class DonationStatus(val key: String) {
    NOT_STARTED("not_started"),
    DEFERRED("deferred"),
    COLLECTED("collected");

    companion object {
        fun fromKey(key: String?): DonationStatus =
            entries.firstOrNull { it.key == key?.trim()?.lowercase() } ?: NOT_STARTED
    }
}