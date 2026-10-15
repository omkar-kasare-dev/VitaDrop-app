/*
const { FieldValue } =
    require("firebase-admin/firestore");

const {
    onDocumentCreated
} = require(
    "firebase-functions/v2/firestore"
);

const admin =
    require("firebase-admin");

const {
    notifyNearbyDonors
} = require(
    "../services/notificationService"
);

exports.onRequestCreated =
    onDocumentCreated(
        "requests/{requestId}",

        async (event) => {

            const requestId =
                event.params.requestId;

            const data =
                event.data.data();

            console.log(
                "New Request:",
                requestId
            );

            await notifyNearbyDonors(
                data,
                5
            );

            await admin
                .firestore()
                .collection("requests")
                .doc(requestId)
                .update({

                    notificationRadius: 5,

                    notificationStartedAt:
                        FieldValue.serverTimestamp()
                });

            return null;
        }
    );
*/

const {
    FieldValue
} = require("firebase-admin/firestore");

const {
    onDocumentCreated
} = require(
    "firebase-functions/v2/firestore"
);

const {
    notifyNearbyDonors
} = require(
    "../services/notificationService"
);

const {
    RADIUS_STEPS_KM
} = require("../utils/config");

/**
 * Runs the moment a hospital creates a request (requests/{requestId}).
 * Step 1 of the alert: donors inside the FIRST radius (5 km).
 */
exports.onRequestCreated =
    onDocumentCreated(
        "requests/{requestId}",

        async (event) => {

            const requestId =
                event.params.requestId;

            const ref =
                event.data.ref;

            const request = {
                ...event.data.data(),
                requestId
            };

            console.log(
                "New Request:",
                requestId
            );

            // Firebase can deliver the same event twice. If this request was already alerted,
            // do nothing, otherwise every donor would get the push twice.
            const fresh = await ref.get();

            if (fresh.get("lastNotifiedAt")) {

                console.log(
                    "Already alerted, skipping:",
                    requestId
                );

                return null;
            }

            const radiusKm = RADIUS_STEPS_KM[0];

            const { notifiedDonorIds } =
                await notifyNearbyDonors(
                    request,
                    radiusKm
                );

            const update = {

                notificationRadius: radiusKm,

                // The scheduler waits EXPANSION_INTERVAL_MINUTES after this moment
                lastNotifiedAt:
                    FieldValue.serverTimestamp()
            };

            if (notifiedDonorIds.length > 0) {

                update.notifiedDonors =
                    FieldValue.arrayUnion(
                        ...notifiedDonorIds
                    );
            }

            await ref.update(update);

            return null;
        }
    );