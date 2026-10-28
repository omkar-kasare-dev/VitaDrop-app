const admin = require("firebase-admin");

const {
    FieldValue,
    Timestamp
} = require("firebase-admin/firestore");

const {
    STATUS,
    OPEN,
    normalizeStatus,
    deriveStatus
} = require("../utils/requestStatus");

const {
    EXPANSION_INTERVAL_MINUTES
} = require("../utils/config");

/** An error the caller may see. "code" is a Firebase callable error code. */
class ActionError extends Error {
    constructor(code, message) {
        super(message);
        this.code = code;
    }
}

/**
 * Only the hospital that owns the request (verified, not blocked) may change it.
 * actorUid === null means "the system" (the admin script).
 * Must be called BEFORE the transaction writes anything.
 */
async function assertCanManage(tx, request, actorUid) {

    if (actorUid === null) {
        return;
    }

    if (request.hospitalId !== actorUid) {
        throw new ActionError("permission-denied", "Only the hospital that created this request can change it.");
    }

    const hospital = await tx.get(admin.firestore().collection("hospitals").doc(actorUid));

    if (!hospital.exists || hospital.get("isVerified") !== true || hospital.get("isBlocked") === true) {
        throw new ActionError("permission-denied", "This hospital account is not verified.");
    }
}

/**
 * The hospital confirms how many units have really been ARRANGED for this request
 * (received from stock or from a donation, by its own staff).
 *
 * It is the number itself, not "+1", so pressing the button twice or retrying never counts
 * a unit twice. A donor tapping "I can donate" never changes it.
 * The status is derived from it (partially_fulfilled / fulfilled), and lowering it again
 * (a mistake) re-opens the request.
 */
async function setUnitsFulfilled({ requestId, units, actorUid }) {

    const count = Number(units);

    if (!Number.isInteger(count) || count < 0) {
        throw new ActionError("invalid-argument", "Units must be a whole number, 0 or more.");
    }

    const db = admin.firestore();
    const ref = db.collection("requests").doc(String(requestId || "-"));

    return db.runTransaction(async (tx) => {

        const snap = await tx.get(ref);

        if (!snap.exists) {
            throw new ActionError("not-found", "Request not found.");
        }

        const request = snap.data();

        await assertCanManage(tx, request, actorUid);

        const required = Number(request.unitsRequired) || 1;

        if (count > required) {
            throw new ActionError("invalid-argument", `This request needs ${required} unit(s), so you cannot confirm more.`);
        }

        const current = normalizeStatus(request.status);

        if (!OPEN.includes(current) && current !== STATUS.FULFILLED) {
            throw new ActionError("failed-precondition", `This request is ${current} and can no longer be changed.`);
        }

        const status = deriveStatus({
            unitsRequired: required,
            unitsFulfilled: count,
            responseCount: request.responseCount
        });

        const update = {
            unitsFulfilled: count,
            status,
            updatedAt: FieldValue.serverTimestamp(),
            fulfillmentUpdatedBy: actorUid || "system"
        };

        if (status === STATUS.FULFILLED) {

            // Everything arranged: no more alerts
            update.fulfilledAt = FieldValue.serverTimestamp();
            update.claimUntil = FieldValue.delete();
            update.nextExpansionAt = FieldValue.delete();

        } else if (current === STATUS.FULFILLED) {

            // A correction: it needs donors again, so the alert may continue
            update.fulfilledAt = FieldValue.delete();
            update.nextExpansionAt = Timestamp.fromMillis(Date.now() + EXPANSION_INTERVAL_MINUTES * 60 * 1000);
        }

        tx.update(ref, update);

        return { status, unitsFulfilled: count, unitsRequired: required };
    });
}

/** The hospital does not need the blood any more. */
async function cancelRequest({ requestId, reason, actorUid }) {

    const db = admin.firestore();
    const ref = db.collection("requests").doc(String(requestId || "-"));

    const cleanReason = String(reason || "").trim().slice(0, 200);

    return db.runTransaction(async (tx) => {

        const snap = await tx.get(ref);

        if (!snap.exists) {
            throw new ActionError("not-found", "Request not found.");
        }

        const request = snap.data();

        await assertCanManage(tx, request, actorUid);

        const current = normalizeStatus(request.status);

        if (!OPEN.includes(current)) {
            throw new ActionError("failed-precondition", `This request is already ${current}.`);
        }

        tx.update(ref, {
            status: STATUS.CANCELLED,
            cancelledAt: FieldValue.serverTimestamp(),
            cancelReason: cleanReason,
            cancelledBy: actorUid || "system",
            updatedAt: FieldValue.serverTimestamp(),
            claimUntil: FieldValue.delete(),
            nextExpansionAt: FieldValue.delete()
        });

        return { status: STATUS.CANCELLED };
    });
}

module.exports = {
    ActionError,
    setUnitsFulfilled,
    cancelRequest
};