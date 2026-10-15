const {
    onSchedule
} = require(
    "firebase-functions/v2/scheduler"
);

const admin =
    require("firebase-admin");

const {
    FieldValue
} = require("firebase-admin/firestore");

const {
    notifyNearbyDonors
} = require(
    "../services/notificationService"
);

const {
    RADIUS_STEPS_KM,
    EXPANSION_INTERVAL_MINUTES
} = require("../utils/config");

// Next radius bigger than the current one, or undefined when the maximum is reached
function nextRadiusAfter(currentRadius) {
    return RADIUS_STEPS_KM.find((radius) => radius > currentRadius);
}

/**
 * Widens the search for requests that still need donors.
 *
 * What was wrong before:
 *  - it widened EVERY pending request on EVERY tick, so a request created 1 minute ago was
 *    already pushed to 10 km. Now a request waits EXPANSION_INTERVAL_MINUTES after its last alert.
 *  - donors already alerted at 5 km were alerted again at 10 km and 15 km.
 *    Now request.notifiedDonors is respected and updated.
 *  - one failing request stopped the whole run. Now each request is handled on its own.
 *
 * It checks every minute so the 5-minute steps are accurate (a run is cheap: one query).
 */
exports.radiusExpansionScheduler =
    onSchedule(

        {
            schedule: "* * * * *",     // every minute
            timeZone: "Asia/Kolkata"
        },

        async () => {

            const snapshot =
                await admin
                    .firestore()
                    .collection("requests")
                    .where(
                        "status",
                        "==",
                        "pending"
                    )
                    .get();

            const now = Date.now();

            for (const doc of snapshot.docs) {

                try {

                    const request = {
                        ...doc.data(),
                        requestId: doc.id
                    };

                    const currentRadius =
                        request.notificationRadius || RADIUS_STEPS_KM[0];

                    const nextRadius =
                        nextRadiusAfter(currentRadius);

                    // Already at the largest radius
                    if (nextRadius === undefined) {
                        continue;
                    }

                    // The first alert is sent by onRequestCreated. Not alerted yet: not our job.
                    const lastAlert =
                        request.lastNotifiedAt;

                    if (!lastAlert || typeof lastAlert.toMillis !== "function") {
                        continue;
                    }

                    const minutesSinceLastAlert =
                        (now - lastAlert.toMillis()) / 60000;

                    if (minutesSinceLastAlert < EXPANSION_INTERVAL_MINUTES) {
                        continue;
                    }

                    console.log(
                        `Expanding ${doc.id} from ${currentRadius} km to ${nextRadius} km`
                    );

                    const { notifiedDonorIds } =
                        await notifyNearbyDonors(
                            request,
                            nextRadius
                        );

                    const update = {

                        notificationRadius: nextRadius,

                        lastNotifiedAt:
                            FieldValue.serverTimestamp()
                    };

                    if (notifiedDonorIds.length > 0) {

                        update.notifiedDonors =
                            FieldValue.arrayUnion(
                                ...notifiedDonorIds
                            );
                    }

                    await doc.ref.update(update);

                } catch (e) {

                    console.error(
                        "Expansion failed for",
                        doc.id,
                        e
                    );
                }
            }

            return null;
        }
    );