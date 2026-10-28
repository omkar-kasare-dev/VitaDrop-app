const { RESPONSE_CAP_FACTOR } = require("./config");

/**
 * The life of a request. One status answers one question: where is this request now?
 * It does NOT say anything about the medical process (testing, compatibility, transfusion).
 *
 *   searching            looking for donors, nobody has said yes yet
 *   arranging            at least one donor said "I can donate"
 *   partially_fulfilled  the hospital confirmed some units, not all
 *   fulfilled            the hospital confirmed all the units it asked for
 *   cancelled            the hospital cancelled it
 *   expired              the deadline passed and no unit was confirmed
 *   unfulfilled          the deadline passed after only some units were confirmed
 */
const STATUS = Object.freeze({
    SEARCHING: "searching",
    ARRANGING: "arranging",
    PARTIALLY_FULFILLED: "partially_fulfilled",
    FULFILLED: "fulfilled",
    CANCELLED: "cancelled",
    EXPIRED: "expired",
    UNFULFILLED: "unfulfilled"
});

// Values written before Step 3 -> the new name
const LEGACY = Object.freeze({
    pending: STATUS.SEARCHING,
    accepted: STATUS.ARRANGING,
    completed: STATUS.FULFILLED,
    closed: STATUS.UNFULFILLED
});

// Still needs attention (alerts, deadline)
const OPEN = Object.freeze([STATUS.SEARCHING, STATUS.ARRANGING, STATUS.PARTIALLY_FULFILLED]);

// Every value that can be STORED for an open request, old names included (for Firestore "in" queries)
const OPEN_STORED = Object.freeze(["pending", "searching", "accepted", "arranging", "partially_fulfilled"]);

function normalizeStatus(value) {

    const key = String(value || "").trim().toLowerCase();

    if (LEGACY[key]) {
        return LEGACY[key];
    }

    return Object.values(STATUS).includes(key) ? key : STATUS.SEARCHING;
}

function isOpen(status) {
    return OPEN.includes(normalizeStatus(status));
}

/** The status that matches the confirmed units and the responses. Only for open / fulfilled requests. */
function deriveStatus(request) {

    const required = Number(request.unitsRequired) || 1;
    const fulfilled = Number(request.unitsFulfilled) || 0;

    if (fulfilled >= required) {
        return STATUS.FULFILLED;
    }

    if (fulfilled > 0) {
        return STATUS.PARTIALLY_FULFILLED;
    }

    if ((Number(request.responseCount) || 0) > 0) {
        return STATUS.ARRANGING;
    }

    return STATUS.SEARCHING;
}

/** How a request that ran out of time is closed. */
function expiryStatus(request) {
    return (Number(request.unitsFulfilled) || 0) > 0 ? STATUS.UNFULFILLED : STATUS.EXPIRED;
}

/** Should the alert keep widening? */
function needsMoreDonors(request) {

    const required = Number(request.unitsRequired) || 1;
    const fulfilled = Number(request.unitsFulfilled) || 0;

    if (fulfilled >= required) {
        return false;
    }

    return (Number(request.responseCount) || 0) < required * RESPONSE_CAP_FACTOR;
}

module.exports = {
    STATUS,
    OPEN,
    OPEN_STORED,
    normalizeStatus,
    isOpen,
    deriveStatus,
    expiryStatus,
    needsMoreDonors
};