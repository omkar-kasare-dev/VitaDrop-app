const admin = require("firebase-admin");

const {
    COMPATIBILITY,
    normalizeBloodGroup
} = require("../utils/bloodCompatibility");

const {
    isValidLocation,
    distanceKm
} = require("./geoService");

// STEP 1: push tokens live in devices/{uid} (private), not in donors/hospitals documents
const {
    getTokens,
    removeTokens
} = require("./deviceService");

const {
    NOTIFICATION_CHANNEL_ID,
    MIN_DAYS_BETWEEN_DONATIONS,
    FCM_TTL_MS
} = require("../utils/config");

// FCM accepts up to 500 tokens in one call
const MULTICAST_LIMIT = 500;

// FCM says these tokens are dead (user logged out, app reinstalled, token rotated)
const INVALID_TOKEN_CODES = new Set([
    "messaging/registration-token-not-registered",
    "messaging/invalid-registration-token"
]);

function normalizeText(value) {
    return String(value || "").trim().toLowerCase();
}

function donatedRecently(donor) {

    const last = donor.lastDonationDate;

    if (!last || typeof last.toMillis !== "function") {
        return false;
    }

    const days = (Date.now() - last.toMillis()) / (24 * 60 * 60 * 1000);

    return days < MIN_DAYS_BETWEEN_DONATIONS;
}

/**
 * Finds eligible donors for one emergency request and alerts them.
 *
 * Returns the ids of the donors that were really notified, so the caller can remember them
 * (request.notifiedDonors) and never alert the same donor twice when the radius widens.
 *
 * Everything that is skipped is counted and logged in ONE summary line:
 * read it in Firebase Console -> Functions -> Logs to see why nobody was notified.
 */
async function notifyNearbyDonors(
    requestData,
    radiusKm
) {

    const db = admin.firestore();

    const requestId = String(requestData.requestId || "");

    const summary = {
        requestId,
        radiusKm,
        requestBloodGroup: requestData.bloodGroup || null,
        compatibleGroups: [],
        donorsWithCompatibleGroup: 0,
        skippedBlocked: 0,
        skippedAlreadyNotified: 0,
        skippedNoToken: 0,
        noTokenDonorIds: [],   // who has no devices/{uid} token: log in again on that phone
        skippedDonatedRecently: 0,
        skippedNoLocation: 0,
        skippedTooFar: 0,
        matchedByCityOnly: 0,
        sent: 0,
        failed: 0,
        removedDeadTokens: 0
    };

    // ---------------- 1. WHICH BLOOD GROUPS CAN HELP? ----------------
    const bloodGroup = normalizeBloodGroup(requestData.bloodGroup);

    if (!bloodGroup) {

        console.warn(
            "Request blood group is not valid, nobody notified:",
            JSON.stringify(summary)
        );

        return { notifiedDonorIds: [], summary };
    }

    const compatibleGroups = COMPATIBILITY[bloodGroup];
    summary.compatibleGroups = compatibleGroups;

    // ---------------- 2. WHERE IS THE PATIENT? ----------------
    const requestLat = requestData.location?.latitude;
    const requestLng = requestData.location?.longitude;

    const requestHasLocation = isValidLocation(requestLat, requestLng);
    const requestCity = normalizeText(requestData.city);

    if (!requestHasLocation && !requestCity) {

        console.warn(
            "Request has neither a valid location nor a city, nobody notified:",
            JSON.stringify(summary)
        );

        return { notifiedDonorIds: [], summary };
    }

    // ---------------- 3. WHO IS AVAILABLE AND COMPATIBLE? ----------------
    const donorsSnapshot = await db
        .collection("donors")
        .where("isAvailable", "==", true)
        .where("bloodGroup", "in", compatibleGroups)
        .get();

    summary.donorsWithCompatibleGroup = donorsSnapshot.size;

    if (donorsSnapshot.empty) {

        console.warn(
            "No AVAILABLE donor has one of these EXACT blood group values " +
            compatibleGroups.join(", ") +
            ". Check the bloodGroup text saved in donors/{uid}.",
            JSON.stringify(summary)
        );

        return { notifiedDonorIds: [], summary };
    }

    const alreadyNotified = new Set(requestData.notifiedDonors || []);

    // ---------------- 4. ELIGIBLE + NEARBY ----------------
    // "let": the list is filtered again below, once the push tokens are known
    let candidates = [];

    for (const doc of donorsSnapshot.docs) {

        const donor = doc.data();

        if (donor.isBlocked === true) {
            summary.skippedBlocked++;
            continue;
        }

        if (alreadyNotified.has(doc.id)) {
            summary.skippedAlreadyNotified++;
            continue;
        }

        if (donatedRecently(donor)) {
            summary.skippedDonatedRecently++;
            continue;
        }

        const donorLat = donor.location?.latitude;
        const donorLng = donor.location?.longitude;

        let distance = null;

        if (requestHasLocation && isValidLocation(donorLat, donorLng)) {

            // Normal case: Haversine distance
            distance = distanceKm(requestLat, requestLng, donorLat, donorLng);

            if (distance > radiusKm) {
                summary.skippedTooFar++;
                continue;
            }

        } else if (requestCity && normalizeText(donor.city) === requestCity) {

            // Safety net: GPS was missing on the request or on the donor,
            // but both are in the same city, so the donor is still alerted.
            summary.matchedByCityOnly++;

        } else {

            summary.skippedNoLocation++;
            continue;
        }

        candidates.push({
            id: doc.id,
            distance: distance === null ? Number.MAX_SAFE_INTEGER : distance
        });
    }

    // STEP 1: tokens come from devices/{uid}. A donor without a token cannot be alerted.
    const tokens = await getTokens(candidates.map((c) => c.id));

    candidates = candidates.filter((c) => {

        c.token = tokens.get(c.id);

        if (!c.token) {
            summary.skippedNoToken++;
            summary.noTokenDonorIds.push(c.id);
            return false;
        }

        return true;
    });

    // Nearest donors first: they can reach the hospital sooner
    candidates.sort((a, b) => a.distance - b.distance);

    // ---------------- 5. SEND (500 donors per call, in parallel) ----------------
    const notifiedDonorIds = [];

    const units = Number(requestData.unitsRequired) || 1;
    const hospitalName = requestData.hospitalName || "A hospital";

    for (let i = 0; i < candidates.length; i += MULTICAST_LIMIT) {

        const chunk = candidates.slice(i, i + MULTICAST_LIMIT);

        try {

            const response = await admin.messaging().sendEachForMulticast({

                tokens: chunk.map((c) => c.token),

                notification: {
                    title: `${bloodGroup} blood needed urgently`,
                    body: `${hospitalName} needs ${units} unit(s) of ${bloodGroup} blood`
                },

                data: {
                    type: "emergency_request",
                    requestId,
                    bloodGroup,
                    hospitalName: String(hospitalName),
                    city: String(requestData.city || ""),
                    radius: String(radiusKm)
                },

                android: {
                    priority: "high",
                    ttl: FCM_TTL_MS,
                    notification: {
                        channelId: NOTIFICATION_CHANNEL_ID
                    }
                }
            });

            const deadTokenDonorIds = [];

            response.responses.forEach((result, index) => {

                const candidate = chunk[index];

                if (result.success) {
                    notifiedDonorIds.push(candidate.id);
                    summary.sent++;
                    return;
                }

                summary.failed++;

                const code = result.error && result.error.code;

                if (INVALID_TOKEN_CODES.has(code)) {
                    deadTokenDonorIds.push(candidate.id);
                } else {
                    console.error(
                        "FCM error for donor",
                        candidate.id,
                        code,
                        result.error && result.error.message
                    );
                }
            });

            // Remove dead tokens so they are not tried again at the next radius
            if (deadTokenDonorIds.length > 0) {

                await removeTokens(deadTokenDonorIds);

                summary.removedDeadTokens += deadTokenDonorIds.length;
            }

        } catch (e) {

            summary.failed += chunk.length;

            console.error("FCM multicast failed:", e);
        }
    }

    console.log("notifyNearbyDonors", JSON.stringify(summary));

    return { notifiedDonorIds, summary };
}

/**
 * Tells the hospital that a donor accepted (flow picture: "Donor Confirmation" -> Hospital).
 * Uses the hospital's own push token (devices/{uid}.token, saved at login).
 */
async function notifyHospitalOfResponse(
    request,
    donorId,
    responseCount,
    unitsNeeded
) {

    const db = admin.firestore();

    const hospitalId = request.hospitalId;

    if (!hospitalId) {
        console.log("Request has no hospitalId, hospital not notified:", request.requestId);
        return;
    }

    const [tokens, donorDoc] = await Promise.all([
        getTokens([hospitalId]),
        db.collection("donors").doc(donorId).get()
    ]);

    const token = tokens.get(hospitalId);

    if (!token) {
        console.log("Hospital has no device token (devices/" + hospitalId + "), not notified");
        return;
    }

    const donorName = donorDoc.get("fullName") || "A donor";
    const patient = request.patientName || "your patient";

    try {

        await admin.messaging().send({

            token,

            notification: {
                title: `Donor responded (${responseCount}/${unitsNeeded})`,
                body: `${donorName} can donate ${request.bloodGroup} for ${patient}`
            },

            data: {
                type: "donor_response",
                requestId: String(request.requestId || ""),
                donorId
            },

            android: {
                priority: "high",
                ttl: FCM_TTL_MS,
                notification: {
                    channelId: NOTIFICATION_CHANNEL_ID
                }
            }
        });

    } catch (e) {

        if (INVALID_TOKEN_CODES.has(e.code)) {
            await removeTokens([hospitalId]);
        } else {
            console.error("Could not notify hospital:", e);
        }
    }
}

module.exports = {
    notifyNearbyDonors,
    notifyHospitalOfResponse
};