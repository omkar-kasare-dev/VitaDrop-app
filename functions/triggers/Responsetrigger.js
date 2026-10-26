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
 * Runs when a donor taps "I can donate": the app writes
 * requests/{requestId}/responses/{donorId}.
 *
 *  - counts the response on the request (in a transaction, so two donors answering at the
 *    same moment never lose a count)
 *  - tells the hospital
 *
 * STEP 2: the response document remembers what was done ("counted", "hospitalNotified"), so a
 * redelivered event does not count the donor twice but DOES still send a push that was lost.
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

            const responseRef =
                event.data.ref;

            const result = await db.runTransaction(async (tx) => {

                // all reads first, then the writes
                const [requestSnap, responseSnap] = await Promise.all([
                    tx.get(requestRef),
                    tx.get(responseRef)
                ]);

                if (!requestSnap.exists || !responseSnap.exists) {
                    return null;
                }

                const request = requestSnap.data();
                const response = responseSnap.data();

                const unitsNeeded =
                    Number(request.unitsRequired) || 1;

                const acceptedDonors = request.acceptedDonors || [];

                // Event delivered again: do not count this donor twice
                if (response.counted === true || acceptedDonors.includes(donorId)) {

                    return {
                        request: { ...request, requestId },
                        responseCount: Number(request.responseCount) || acceptedDonors.length || 1,
                        unitsNeeded,
                        needsPush: response.hospitalNotified !== true
                    };
                }

                const responseCount =
                    (Number(request.responseCount) || 0) + 1;

                const update = {
                    responseCount,
                    acceptedDonors: FieldValue.arrayUnion(donorId)
                };

                if (!request.acceptedBy) {
                    update.acceptedBy = donorId;   // first donor who said yes
                }

                // Enough donors said yes: stop widening the search
                // (Step 3 replaces this with confirmed units)
                if (request.status === "pending" && responseCount >= unitsNeeded) {
                    update.status = "accepted";
                }

                tx.update(requestRef, update);
                tx.update(responseRef, { counted: true });

                return {
                    request: { ...request, requestId },
                    responseCount,
                    unitsNeeded,
                    needsPush: true
                };
            });

            if (!result || !result.needsPush) {
                return null;
            }

            const sent = await notifyHospitalOfResponse(
                result.request,
                donorId,
                result.responseCount,
                result.unitsNeeded
            );

            if (sent) {
                await responseRef.update({ hospitalNotified: true });
            }

            return null;
        }
    );