const admin = require("firebase-admin");

const {
    FieldValue,
    Timestamp
} = require("firebase-admin/firestore");

const {
    ActionError,
    assertCanManage
} = require("./fulfillmentService");

const {
    STATUS,
    isOpen,
    normalizeStatus,
    deriveStatus,
    needsMoreDonors
} = require("../utils/requestStatus");

const {
    RESPONSE,
    ATTENDANCE,
    DONATION,
    ATTENDANCE_VALUES,
    DONATION_VALUES,
    readResponse,
    summarize
} = require("../utils/responseStatus");

const {
    RADIUS_STEPS_KM,
    EXPANSION_INTERVAL_MINUTES
} = require("../utils/config");

function millis(value) {
    return value && typeof value.toMillis === "function" ? value.toMillis() : null;
}

// Same content, same text, whatever the order of the keys Firestore gives back
function stable(value) {
    if (Array.isArray(value)) return JSON.stringify([...value].sort());
    if (value && typeof value === "object") {
        return JSON.stringify(Object.keys(value).sort().map((k) => [k, value[k]]));
    }
    return JSON.stringify(value ?? null);
}

/**
 * Makes the request document agree with its responses. It is computed from ALL the
 * responses every time, never "+1 / -1", so a retried or duplicated event cannot make it drift.
 *
 *  - responseCount    donors who are still expected (accepted, not a no-show, not deferred)
 *  - acceptedDonors   their ids,   rejectedDonors   ids of donors who declined or withdrew
 *  - responseSummary  the numbers a hospital screen needs
 *  - status           searching / arranging / partially_fulfilled, from those numbers
 *  - when donors drop out, the search is switched on again (nextExpansionAt)
 */
async function syncRequestWithResponses(requestId) {

    const db = admin.firestore();

    const requestRef = db.collection("requests").doc(String(requestId));

    return db.runTransaction(async (tx) => {

        const [requestSnap, responsesSnap] = await Promise.all([
            tx.get(requestRef),
            tx.get(requestRef.collection("responses"))
        ]);

        if (!requestSnap.exists) {
            return null;
        }

        const request = requestSnap.data();

        const responses = responsesSnap.docs.map((d) => ({ id: d.id, ...readResponse(d.data()) }));

        const { counts, activeIds, declinedIds } = summarize(responses);

        const update = {};

        if ((Number(request.responseCount) || 0) !== counts.active) {
            update.responseCount = counts.active;
        }

        if (stable(request.acceptedDonors || []) !== stable(activeIds)) {
            update.acceptedDonors = activeIds;
        }

        if (stable(request.rejectedDonors || []) !== stable(declinedIds)) {
            update.rejectedDonors = declinedIds;
        }

        if (stable(request.responseSummary) !== stable(counts)) {
            update.responseSummary = counts;
        }

        const merged = { ...request, responseCount: counts.active };

        if (isOpen(request.status)) {

            const current = normalizeStatus(request.status);
            const next = deriveStatus(merged);

            if (next !== STATUS.FULFILLED && next !== current) {
                update.status = next;
            } else if (request.status !== current) {
                update.status = current;                       // old name -> new name
            }

            // Donors dropped out: if the search had stopped, switch it on again
            const radius = Number(request.notificationRadius) || RADIUS_STEPS_KM[0];
            const canWiden = RADIUS_STEPS_KM.some((r) => r > radius);

            if (needsMoreDonors(merged) && canWiden && request.lastNotifiedAt &&
                !request.nextExpansionAt && !request.claimUntil && !request.alertError) {

                update.nextExpansionAt =
                    Timestamp.fromMillis(Date.now() + EXPANSION_INTERVAL_MINUTES * 60 * 1000);
            }
        }

        if (Object.keys(update).length > 0) {
            tx.update(requestRef, update);
        }

        return { ...counts, status: update.status || normalizeStatus(request.status) };
    });
}

/**
 * The hospital records what happened to ONE donor who said yes:
 * did the donor arrive, and was a donation collected or deferred.
 *
 * This is the interim rule from the roadmap: until blood centres have their own accounts, the
 * hospital's authorised staff report it. It never changes unitsFulfilled: a collected donation
 * is not a unit for the patient until it is tested, released and confirmed by the hospital.
 *
 * "collected" also updates the donor's lastDonationDate, so the donor is not alerted again
 * before MIN_DAYS_BETWEEN_DONATIONS. Marking it again as not collected restores the old date.
 */
async function setResponseOutcome({ requestId, donorId, attendance, donation, actorUid }) {

    if (attendance === undefined && donation === undefined) {
        throw new ActionError("invalid-argument", "Nothing to change.");
    }

    if (attendance !== undefined && !ATTENDANCE_VALUES.includes(attendance)) {
        throw new ActionError("invalid-argument", "Unknown attendance value.");
    }

    if (donation !== undefined && !DONATION_VALUES.includes(donation)) {
        throw new ActionError("invalid-argument", "Unknown donation value.");
    }

    const db = admin.firestore();

    const requestRef = db.collection("requests").doc(String(requestId || "-"));
    const responseRef = requestRef.collection("responses").doc(String(donorId || "-"));
    const donorRef = db.collection("donors").doc(String(donorId || "-"));

    const result = await db.runTransaction(async (tx) => {

        const [requestSnap, responseSnap, donorSnap] = await Promise.all([
            tx.get(requestRef),
            tx.get(responseRef),
            tx.get(donorRef)
        ]);

        if (!requestSnap.exists) {
            throw new ActionError("not-found", "Request not found.");
        }

        if (!responseSnap.exists) {
            throw new ActionError("not-found", "This donor has not responded to this request.");
        }

        await assertCanManage(tx, requestSnap.data(), actorUid);

        const current = readResponse(responseSnap.data());

        if (current.responseStatus !== RESPONSE.ACCEPTED) {
            throw new ActionError("failed-precondition", "Only a donor who said \"I can donate\" can be marked.");
        }

        const nextAttendance = attendance ?? current.attendanceStatus;

        let nextDonation = donation ?? current.donationStatus;

        if (nextAttendance !== ATTENDANCE.ARRIVED) {

            if (donation !== undefined && donation !== DONATION.NOT_STARTED) {
                throw new ActionError("invalid-argument", "Mark the donor as arrived first.");
            }

            nextDonation = DONATION.NOT_STARTED;       // not here, so nothing was collected or deferred
        }

        const update = {
            attendanceStatus: nextAttendance,
            donationStatus: nextDonation,
            outcomeUpdatedAt: FieldValue.serverTimestamp(),
            outcomeUpdatedBy: actorUid || "system"
        };

        const wasCollected = current.donationStatus === DONATION.COLLECTED;
        const willBeCollected = nextDonation === DONATION.COLLECTED;

        if (willBeCollected && !wasCollected) {

            const nowMs = Date.now();

            update.collectedAt = Timestamp.fromMillis(nowMs);
            update.priorLastDonationDate = donorSnap.exists ? (donorSnap.get("lastDonationDate") || null) : null;

            if (donorSnap.exists) {
                tx.update(donorRef, { lastDonationDate: Timestamp.fromMillis(nowMs) });
            }

        } else if (wasCollected && !willBeCollected) {

            // Undo, but only if nothing newer has replaced the date we wrote
            const mine = millis(responseSnap.get("collectedAt"));
            const donorLast = donorSnap.exists ? millis(donorSnap.get("lastDonationDate")) : null;

            if (mine !== null && donorLast === mine) {
                tx.update(donorRef, { lastDonationDate: responseSnap.get("priorLastDonationDate") ?? null });
            }

            update.collectedAt = FieldValue.delete();
            update.priorLastDonationDate = FieldValue.delete();
        }

        tx.update(responseRef, update);

        return { attendanceStatus: nextAttendance, donationStatus: nextDonation };
    });

    await syncRequestWithResponses(requestId);

    return result;
}

module.exports = {
    syncRequestWithResponses,
    setResponseOutcome
};