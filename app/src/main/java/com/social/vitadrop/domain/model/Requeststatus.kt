package com.social.vitadrop.domain.model


/**
 * Where a request is in its life. It says nothing about the medical process
 * (testing, compatibility, transfusion): those stay with the blood bank and the clinicians.
 *
 * Old documents still use "pending" and "accepted": fromKey maps them to the new names.
 * isOpen = the request still needs donors or is still being arranged.
 */
enum class RequestStatus(val key: String, val label: String, val isOpen: Boolean) {
    SEARCHING("searching", "Searching for donors", true),
    ARRANGING("arranging", "Donors responding", true),
    PARTIALLY_FULFILLED("partially_fulfilled", "Partly arranged", true),
    FULFILLED("fulfilled", "Fulfilled", false),
    CANCELLED("cancelled", "Cancelled", false),
    EXPIRED("expired", "Expired", false),
    UNFULFILLED("unfulfilled", "Closed, not fully met", false);

    companion object {
        fun fromKey(key: String?): RequestStatus = when (key?.trim()?.lowercase()) {
            "searching", "pending" -> SEARCHING
            "arranging", "accepted" -> ARRANGING
            "partially_fulfilled" -> PARTIALLY_FULFILLED
            "fulfilled", "completed" -> FULFILLED
            "cancelled" -> CANCELLED
            "expired" -> EXPIRED
            "unfulfilled", "closed" -> UNFULFILLED
            else -> SEARCHING    // unknown text counts as open, like before
        }
    }
}