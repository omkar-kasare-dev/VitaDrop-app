const admin = require("firebase-admin");

const {
    FieldValue,
    Timestamp
} = require("firebase-admin/firestore");

const {
    notifyNearbyDonors
} = require("./notificationService");

const {
    OPEN_STORED,
    normalizeStatus,
    isOpen,
    expiryStatus,
    needsMoreDonors
} = require("../utils/requestStatus");

const {
    RADIUS_STEPS_KM,
    EXPANSION_INTERVAL_MINUTES,
    REQUEST_EXPIRY_MINUTES,
    CLAIM_LEASE_SECONDS,
    MAX_ALERT_ATTEMPTS,
    RESCUE_AFTER_MINUTES,
    RESCUE_WINDOW_MINUTES,
    SCHEDULER_BATCH_SIZE
} = require("../utils/config");

const MINUTE = 60 * 1000;
const HOUR = 60 * MINUTE;

const SAVE_TRIES = 3;
const SAVE_RETRY_MS = 500;

// Requests written by the old scheduler / the old app: swept by creation time
const LEGACY_SWEEP_LIMIT = 100;

function millis(value) {
    return value && typeof value.toMillis === "function" ? value.toMillis() : null;
}

function nextRadiusAfter(currentRadius) {
    return RADIUS_STEPS_KM.find((radius) => radius > currentRadius);
}

function sleep(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * STEP 3: a request can carry its own deadline ("needed within N hours", requiredWithinHours).
 *  - requiredBy = createdAt + N hours
 *  - expiresAt  = requiredBy, or createdAt + REQUEST_EXPIRY_MINUTES when no deadline was given
 * The deadline is computed here, from the SERVER time of createdAt, never from a phone clock.
 */
function planDeadlines(request, createdMs) {

    const existingExpiry = millis(request.expiresAt);

    if (createdMs === null) {
        return { requiredByMs: null, expiresMs: existingExpiry };
    }

    const hours = Number(request.requiredWithinHours);

    const requiredByMs = hours > 0 ? createdMs + hours * HOUR : null;

    const expiresMs =
        existingExpiry ??
        requiredByMs ??
        createdMs + REQUEST_EXPIRY_MINUTES * MINUTE;

    return { requiredByMs, expiresMs };
}

/**
 * Step 1 of every alert: CLAIM the request inside a transaction.
 *
 * The transaction re-reads the request, so it knows the CURRENT status (a cancelled, fulfilled
 * or expired request is never alerted) and it is the only place that decides who may send:
 *  - the same step is never sent twice (a duplicate event, or two scheduler runs at once,
 *    finds the lease taken or the next step not due yet)
 *  - the claim also writes a "retry deadline" (nextExpansionAt), so if this function crashes
 *    before it finishes, the scheduler picks the request up again after the lease
 *
 * Returns { outcome, radius?, request? }. Only outcome "claimed" means: go and send.
 */
async function claimStep(ref) {

    const db = admin.firestore();

    return db.runTransaction(async (tx) => {

        const snap = await tx.get(ref);

        if (!snap.exists) {
            return { outcome: "gone" };
        }

        const r = snap.data();
        const now = Date.now();

        const status = normalizeStatus(r.status);

        if (!isOpen(status)) {
            return { outcome: "inactive" };
        }

        // Deadline passed: close it ("expired", or "unfulfilled" when some units were confirmed)
        const createdMs = millis(r.createdAt);
        const { requiredByMs, expiresMs } = planDeadlines(r, createdMs);

        if (expiresMs !== null && now >= expiresMs) {

            tx.update(ref, {
                status: expiryStatus(r),
                expiredAt: FieldValue.serverTimestamp(),
                claimUntil: FieldValue.delete(),
                nextExpansionAt: FieldValue.delete()
            });

            return { outcome: "expired" };
        }

        // STEP 3: the search ends when the hospital confirmed all units, or when enough donors
        // said yes (units x RESPONSE_CAP_FACTOR). A single "I can donate" never ends it.
        if (!needsMoreDonors(r)) {

            if (r.nextExpansionAt || r.claimUntil) {
                tx.update(ref, {
                    nextExpansionAt: FieldValue.delete(),
                    claimUntil: FieldValue.delete()
                });
            }

            return { outcome: "enough_donors" };
        }

        // Somebody else is sending this step right now
        const leaseMs = millis(r.claimUntil);

        if (leaseMs !== null && leaseMs > now) {
            return { outcome: "leased" };
        }

        // The same step failed too many times: stop retrying, keep the reason
        if ((r.alertAttempts || 0) >= MAX_ALERT_ATTEMPTS) {

            if (!r.alertError) {
                tx.update(ref, {
                    alertError: `Gave up after ${MAX_ALERT_ATTEMPTS} failed attempts`,
                    claimUntil: FieldValue.delete(),
                    nextExpansionAt: FieldValue.delete()
                });
            }

            return { outcome: "gave_up" };
        }

        // Which radius is next?
        const lastAlertMs = millis(r.lastNotifiedAt);

        let radius;

        if (lastAlertMs === null) {

            radius = RADIUS_STEPS_KM[0];                 // first alert

        } else {

            radius = nextRadiusAfter(r.notificationRadius || RADIUS_STEPS_KM[0]);

            if (radius === undefined) {

                if (r.nextExpansionAt) {
                    tx.update(ref, { nextExpansionAt: FieldValue.delete() });
                }

                return { outcome: "max_radius" };
            }

            // Requests from the older scheduler have no nextExpansionAt: use lastNotifiedAt
            const dueMs = millis(r.nextExpansionAt) ?? (lastAlertMs + EXPANSION_INTERVAL_MINUTES * MINUTE);

            if (now < dueMs) {
                return { outcome: "not_due" };
            }
        }

        const leaseEnd = Timestamp.fromMillis(now + CLAIM_LEASE_SECONDS * 1000);

        const update = {
            claimUntil: leaseEnd,
            nextExpansionAt: leaseEnd,                   // retry deadline if we crash
            alertAttempts: FieldValue.increment(1)
        };

        // Old names ("pending", "accepted") become the new ones the first time a request is touched
        if (r.status !== status) {
            update.status = status;
        }

        if (!r.expiresAt && expiresMs !== null) {
            update.expiresAt = Timestamp.fromMillis(expiresMs);
        }

        if (!r.requiredBy && requiredByMs !== null) {
            update.requiredBy = Timestamp.fromMillis(requiredByMs);
        }

        tx.update(ref, update);

        return {
            outcome: "claimed",
            radius,
            request: { ...r, requestId: ref.id }
        };
    });
}

/**
 * Step 3 of every alert: remember what was done. Retried, because losing this write
 * means the same donors would be alerted again at the next attempt.
 */
async function saveProgress(ref, radius, notifiedDonorIds) {

    const hasNext = nextRadiusAfter(radius) !== undefined;

    const update = {
        notificationRadius: radius,
        lastNotifiedAt: FieldValue.serverTimestamp(),
        claimUntil: FieldValue.delete(),
        alertAttempts: FieldValue.delete(),
        alertError: FieldValue.delete(),
        nextExpansionAt: hasNext
            ? Timestamp.fromMillis(Date.now() + EXPANSION_INTERVAL_MINUTES * MINUTE)
            : FieldValue.delete()
    };

    if (notifiedDonorIds.length > 0) {
        update.notifiedDonors = FieldValue.arrayUnion(...notifiedDonorIds);
    }

    for (let attempt = 1; attempt <= SAVE_TRIES; attempt++) {

        try {

            await ref.update(update);

            return true;

        } catch (e) {

            console.error(`Could not save alert progress for ${ref.id} (try ${attempt}/${SAVE_TRIES})`, e);

            if (attempt < SAVE_TRIES) {
                await sleep(SAVE_RETRY_MS * attempt);
            }
        }
    }

    // Last resort: the ids are in the log, so nothing is lost silently
    console.error(
        `ALERT PROGRESS LOST for ${ref.id} at ${radius} km. ` +
        `Donors already notified: ${JSON.stringify(notifiedDonorIds)}`
    );

    return false;
}

/**
 * One alert step for one request: claim -> send -> save.
 * Used by onRequestCreated (first alert) and by the scheduler (widening, retries, expiry).
 * Never throws.
 */
async function processRequest(ref) {

    const claim = await claimStep(ref);

    if (claim.outcome !== "claimed") {
        return claim.outcome;
    }

    try {

        const { notifiedDonorIds } =
            await notifyNearbyDonors(claim.request, claim.radius);

        const saved = await saveProgress(ref, claim.radius, notifiedDonorIds);

        return saved ? "alerted" : "alerted_not_saved";

    } catch (e) {

        // The lease expires by itself and the scheduler retries this step
        console.error(`Alert failed for ${ref.id} at ${claim.radius} km, it will be retried:`, e);

        return "failed";
    }
}

/**
 * One scheduler run. It reads only the requests that need something:
 *  1. open requests past their deadline (expiresAt)      -> closed
 *  2. old requests that have no expiresAt yet            -> closed when 24 h old
 *  3. requests whose next step is due (nextExpansionAt)  -> widen, or retry a crashed step
 *  4. requests that were never alerted                   -> first alert (safety net)
 * claimStep() re-checks everything, so a stale query result can never cause a wrong alert.
 */
async function runExpansionTick() {

    const db = admin.firestore();

    const now = Date.now();

    const requests = db.collection("requests");

    const refs = new Map();

    async function collect(label, queryPromise, keep) {

        try {

            const snapshot = await queryPromise;

            snapshot.docs.forEach((d) => {
                if (!keep || keep(d)) {
                    refs.set(d.id, d.ref);
                }
            });

        } catch (e) {

            // In production a missing index shows up here: firebase deploy --only firestore:indexes
            console.error(`Scheduler query "${label}" failed:`, e);
        }
    }

    const open = [...OPEN_STORED];

    await collect(
        "past deadline",
        requests
            .where("status", "in", open)
            .where("expiresAt", "<=", Timestamp.fromMillis(now))
            .limit(SCHEDULER_BATCH_SIZE)
            .get()
    );

    await collect(
        "old without deadline",
        requests
            .where("status", "in", open)
            .where("createdAt", "<=", Timestamp.fromMillis(now - REQUEST_EXPIRY_MINUTES * MINUTE))
            .limit(LEGACY_SWEEP_LIMIT)
            .get(),
        (d) => !d.get("expiresAt")
    );

    await collect(
        "due",
        requests
            .where("status", "in", open)
            .where("nextExpansionAt", "<=", Timestamp.fromMillis(now))
            .limit(SCHEDULER_BATCH_SIZE)
            .get()
    );

    // No limit here on purpose: the 60-minute window keeps it small, and the filter below
    // runs in code, so a limit could hide the stuck requests behind already-alerted ones.
    await collect(
        "never alerted",
        requests
            .where("status", "in", open)
            .where("createdAt", "<=", Timestamp.fromMillis(now - RESCUE_AFTER_MINUTES * MINUTE))
            .where("createdAt", ">=", Timestamp.fromMillis(now - RESCUE_WINDOW_MINUTES * MINUTE))
            .get(),
        (d) => !d.get("lastNotifiedAt") && !d.get("nextExpansionAt") && !d.get("alertError")
    );

    const outcomes = {};

    for (const ref of refs.values()) {

        let outcome;

        try {
            outcome = await processRequest(ref);
        } catch (e) {
            console.error("Alert step failed for", ref.id, e);
            outcome = "error";
        }

        outcomes[outcome] = (outcomes[outcome] || 0) + 1;
    }

    if (refs.size > 0) {
        console.log("radiusExpansionTick", JSON.stringify({ candidates: refs.size, ...outcomes }));
    }

    return outcomes;
}

module.exports = {
    claimStep,
    processRequest,
    runExpansionTick
};