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