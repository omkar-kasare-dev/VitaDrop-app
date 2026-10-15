const admin = require("firebase-admin");

const {
    FieldValue
} = require("firebase-admin/firestore");

const {
    onDocumentCreated
} = require(
    "firebase-functions/v2/firestore"
);

const {
    notifyHospitalOfResponse
} = require(
    "../services/notificationService"
);

/**
 * NEW. Runs when a donor taps "Respond Now": the app writes
 * requests/{requestId}/responses/{donorId}.
 *
 * Before, nothing reacted to that write, so:
 *  - the request stayed "pending" forever and the scheduler kept widening the radius
 *    even after enough donors had accepted
 *  - the hospital was never told (flow picture: "Donor Confirmation" -> Hospital)
 */
exports.onResponseCreated =
    onDocumentCreated(
        "requests/{requestId}/responses/{donorId}",

        async (event) => {

            const {
                requestId,
                donorId
            } = event.params;

            const db = admin.firestore();

            const requestRef =
                db.collection("requests").doc(requestId);

            // Transaction: two donors answering at the same moment must not lose a count
            const result = await db.runTransaction(async (tx) => {

                const snap = await tx.get(requestRef);

                if (!snap.exists) {
                    return null;
                }

                const request = snap.data();

                // Event delivered twice: this donor is already counted
                if ((request.acceptedDonors || []).includes(donorId)) {
                    return null;
                }

                const responseCount =
                    (request.responseCount || 0) + 1;

                const unitsNeeded =
                    Number(request.unitsRequired) || 1;

                const update = {
                    responseCount,
                    acceptedDonors: FieldValue.arrayUnion(donorId)
                };

                if (!request.acceptedBy) {
                    update.acceptedBy = donorId;   // first donor who said yes
                }

                // Enough donors said yes: stop widening the search
                if (request.status === "pending" && responseCount >= unitsNeeded) {
                    update.status = "accepted";
                }

                tx.update(requestRef, update);

                return {
                    request: { ...request, requestId },
                    responseCount,
                    unitsNeeded
                };
            });

            if (!result) {
                return null;
            }

            await notifyHospitalOfResponse(
                result.request,
                donorId,
                result.responseCount,
                result.unitsNeeded
            );

            return null;
        }
    );