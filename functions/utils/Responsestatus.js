/**
 * What a donor's response to a request looks like. Three facts, kept apart on purpose:
 *
 *   responseStatus     accepted | declined | withdrawn        set by the DONOR ("I can donate", "not available")
 *   attendanceStatus   not_arrived | arrived | no_show         set by the HOSPITAL
 *   donationStatus     not_started | deferred | collected       set by the HOSPITAL
 *
 * "accepted" only means the donor is WILLING. It says nothing about eligibility, and "collected"
 * is not a unit of blood for the patient: that needs testing, release and the hospital's own
 * confirmation (request.unitsFulfilled). VitaDrop only records what authorised staff report.
 */
const RESPONSE = Object.freeze({ ACCEPTED: "accepted", DECLINED: "declined", WITHDRAWN: "withdrawn" });
const ATTENDANCE = Object.freeze({ NOT_ARRIVED: "not_arrived", ARRIVED: "arrived", NO_SHOW: "no_show" });
const DONATION = Object.freeze({ NOT_STARTED: "not_started", DEFERRED: "deferred", COLLECTED: "collected" });

const ATTENDANCE_VALUES = Object.values(ATTENDANCE);
const DONATION_VALUES = Object.values(DONATION);

function pick(values, value, fallback) {
    const key = String(value || "").trim().toLowerCase();
    return values.includes(key) ? key : fallback;
}

/**
 * Reads a response document. Documents written before Step 4 only have
 * { status: "accepted", responseTime }: they count as an accepted, not yet arrived response.
 */
function readResponse(data) {

    const d = data || {};

    return {
        responseStatus: pick(Object.values(RESPONSE), d.responseStatus, RESPONSE.ACCEPTED),
        attendanceStatus: pick(ATTENDANCE_VALUES, d.attendanceStatus, ATTENDANCE.NOT_ARRIVED),
        donationStatus: pick(DONATION_VALUES, d.donationStatus, DONATION.NOT_STARTED)
    };
}

/**
 * A response that still counts as "a donor we can expect": willing, not a no-show, not deferred.
 * A donor who already donated (collected) still counts.
 */
function isActive(response) {
    return response.responseStatus === RESPONSE.ACCEPTED &&
        response.attendanceStatus !== ATTENDANCE.NO_SHOW &&
        response.donationStatus !== DONATION.DEFERRED;
}

/** Numbers for the request document, computed from ALL its responses (so retries never drift). */
function summarize(responses) {

    const counts = {
        accepted: 0, declined: 0, withdrawn: 0,
        active: 0, arrived: 0, noShow: 0, deferred: 0, collected: 0
    };

    const activeIds = [];
    const declinedIds = [];

    for (const r of responses) {

        if (r.responseStatus === RESPONSE.ACCEPTED) counts.accepted++;
        if (r.responseStatus === RESPONSE.DECLINED) counts.declined++;
        if (r.responseStatus === RESPONSE.WITHDRAWN) counts.withdrawn++;

        if (r.responseStatus === RESPONSE.DECLINED || r.responseStatus === RESPONSE.WITHDRAWN) {
            declinedIds.push(r.id);
        }

        if (r.responseStatus === RESPONSE.ACCEPTED) {
            if (r.attendanceStatus === ATTENDANCE.ARRIVED) counts.arrived++;
            if (r.attendanceStatus === ATTENDANCE.NO_SHOW) counts.noShow++;
            if (r.donationStatus === DONATION.DEFERRED) counts.deferred++;
            if (r.donationStatus === DONATION.COLLECTED) counts.collected++;
        }

        if (isActive(r)) {
            counts.active++;
            activeIds.push(r.id);
        }
    }

    return { counts, activeIds, declinedIds };
}

module.exports = {
    RESPONSE,
    ATTENDANCE,
    DONATION,
    ATTENDANCE_VALUES,
    DONATION_VALUES,
    readResponse,
    isActive,
    summarize
};