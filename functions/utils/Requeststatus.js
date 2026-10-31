
const { RESPONSE_CAP_FACTOR } = require("./config");

const STATUS = Object.freeze({
    SEARCHING: "searching",
    ARRANGING: "arranging",
    PARTIALLY_FULFILLED: "partially_fulfilled",
    FULFILLED: "fulfilled",
    CANCELLED: "cancelled",
    EXPIRED: "expired",
    UNFULFILLED: "unfulfilled"
});

const LEGACY = Object.freeze({
    pending: STATUS.SEARCHING,
    accepted: STATUS.ARRANGING,
    completed: STATUS.FULFILLED,
    closed: STATUS.UNFULFILLED
});

const OPEN = Object.freeze([
    STATUS.SEARCHING,
    STATUS.ARRANGING,
    STATUS.PARTIALLY_FULFILLED
]);

const OPEN_STORED = Object.freeze([
    "pending",
    "searching",
    "accepted",
    "arranging",
    "partially_fulfilled"
]);

function finiteNumber(value, fallback = 0) {
    const number = Number(value);
    return Number.isFinite(number) ? number : fallback;
}

function normalizeStatus(value) {
    const key = String(value || "").trim().toLowerCase();

    if (Object.prototype.hasOwnProperty.call(LEGACY, key)) {
        return LEGACY[key];
    }

    return Object.values(STATUS).includes(key)
        ? key
        : STATUS.SEARCHING;
}

function isOpen(status) {
    return OPEN.includes(normalizeStatus(status));
}

function deriveStatus(request) {
    const required = Math.max(1, finiteNumber(request.unitsRequired, 1));
    const fulfilled = Math.max(0, finiteNumber(request.unitsFulfilled));
    const responseCount = Math.max(0, finiteNumber(request.responseCount));

    if (fulfilled >= required) {
        return STATUS.FULFILLED;
    }

    if (fulfilled > 0) {
        return STATUS.PARTIALLY_FULFILLED;
    }

    if (responseCount > 0) {
        return STATUS.ARRANGING;
    }

    return STATUS.SEARCHING;
}

function expiryStatus(request) {
    return finiteNumber(request.unitsFulfilled) > 0
        ? STATUS.UNFULFILLED
        : STATUS.EXPIRED;
}

function needsMoreDonors(request) {
    const required = Math.max(1, finiteNumber(request.unitsRequired, 1));
    const fulfilled = Math.max(0, finiteNumber(request.unitsFulfilled));
    const responseCount = Math.max(0, finiteNumber(request.responseCount));
    const capFactor = finiteNumber(RESPONSE_CAP_FACTOR, 1);

    if (fulfilled >= required) {
        return false;
    }

    return responseCount < required * capFactor;
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
