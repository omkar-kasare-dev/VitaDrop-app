const {
    onSchedule
} = require(
    "firebase-functions/v2/scheduler"
);

const admin =
    require("firebase-admin");

const {
    notifyNearbyDonors
} = require(
    "../services/notificationService"
);

exports.radiusExpansionScheduler =
    onSchedule(

        {
            schedule: "every 5 minutes",
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

            for (
                const doc of snapshot.docs
            ) {

                const request =
                    doc.data();

                const currentRadius =
                    request.notificationRadius || 5;

                /**
                 * STOP IF ACCEPTED
                 */
                if (
                    request.status ===
                    "accepted"
                ) {
                    continue;
                }

                let nextRadius = null;

                if (
                    currentRadius === 5
                ) {

                    nextRadius = 10;

                } else if (
                    currentRadius === 10
                ) {

                    nextRadius = 15;
                }

                if (
                    nextRadius == null
                ) {
                    continue;
                }

                console.log(
                    `Expanding ${doc.id}
                     from ${currentRadius}
                     to ${nextRadius}`
                );

                await notifyNearbyDonors(
                    request,
                    nextRadius
                );

                await doc.ref.update({

                    notificationRadius:
                        nextRadius
                });
            }

            return null;
        }
    );