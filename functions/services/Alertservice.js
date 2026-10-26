const admin = require("firebase-admin");

const {
    FieldValue,
    Timestamp
} = require("firebase-admin/firestore");

const {
    notifyNearbyDonors
} = require("./notificationService");

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

// Requests that can still be alerted ("searching" is the name planned for Step 3)
const SEARCHING = new Set(["pending", "searching"]);

// Requests that are still open: they are closed after REQUEST_EXPIRY_MINUTES
const OPEN = new Set(["pending", "searching", "accepted"]);

const SAVE_TRIES = 3;
const SAVE_RETRY_MS = 500;

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
 * Step 1 of every alert: CLAIM the request inside a transaction.
 *
 * The transaction re-reads the request, so it knows the CURRENT status (a cancelled or
 * closed request is never alerted) and it is the only place that decides who may send:
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

        if (!OPEN.has(r.status)) {
            return { outcome: "inactive" };
        }

        // Too old: close it
        const createdMs = millis(r.createdAt);

        if (createdMs !== null && now >= createdMs + REQUEST_EXPIRY_MINUTES * MINUTE) {

            tx.update(ref, {
                status: "expired",
                expiredAt: FieldValue.serverTimestamp(),
                claimUntil: FieldValue.delete(),
                nextExpansionAt: FieldValue.delete()
            });

            return { outcome: "expired" };
        }

        // Open but not searching (for example enough donors answered): nothing to send
        if (!SEARCHING.has(r.status)) {
            return { outcome: "not_searching" };
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

        if (!r.expiresAt && createdMs !== null) {
            update.expiresAt = Timestamp.fromMillis(createdMs + REQUEST_EXPIRY_MINUTES * MINUTE);
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
 *  1. open requests older than REQUEST_EXPIRY_MINUTES   -> closed as "expired"
 *  2. requests whose next step is due (nextExpansionAt)  -> widen, or retry a crashed step
 *  3. requests that were never alerted at all            -> first alert (safety net)
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

    const expireBefore = Timestamp.fromMillis(now - REQUEST_EXPIRY_MINUTES * MINUTE);

    for (const status of ["pending", "accepted"]) {
        await collect(
            `expire ${status}`,
            requests
                .where("status", "==", status)
                .where("createdAt", "<=", expireBefore)
                .limit(SCHEDULER_BATCH_SIZE)
                .get()
        );
    }

    await collect(
        "due",
        requests
            .where("status", "==", "pending")
            .where("nextExpansionAt", "<=", Timestamp.fromMillis(now))
            .limit(SCHEDULER_BATCH_SIZE)
            .get()
    );

    // No limit here on purpose: the 60-minute window keeps it small, and the filter below
    // runs in code, so a limit could hide the stuck requests behind already-alerted ones.
    await collect(
        "never alerted",
        requests
            .where("status", "==", "pending")
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