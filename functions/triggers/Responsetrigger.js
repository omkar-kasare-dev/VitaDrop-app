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

const {
    STATUS,
    normalizeStatus,
    isOpen,
    deriveStatus
} = require(
    "../utils/requestStatus"
);

/**
 * Runs when a donor taps "I can donate": the app writes
 * requests/{requestId}/responses/{donorId}.
 *
 *  - counts the response on the request (in a transaction, so two donors answering at the
 *    same moment never lose a count)
 *  - tells the hospital
 *
 * STEP 3: a response is a donor who is WILLING. It is not a unit of blood, so it no longer
 * closes the request or stops the search. It only moves "searching" to "arranging". The
 * request is fulfilled when the HOSPITAL confirms the units (fulfillmentService).
 *
 * The response document remembers what was done ("counted", "hospitalNotified"), so a
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

                // An open request moves to the status that matches its numbers.
                // (A fulfilled / cancelled / expired request keeps its status.)
                if (isOpen(request.status)) {

                    const next = deriveStatus({
                        unitsRequired: unitsNeeded,
                        unitsFulfilled: request.unitsFulfilled,
                        responseCount
                    });

                    if (next !== STATUS.FULFILLED && next !== normalizeStatus(request.status)) {
                        update.status = next;
                    } else if (request.status !== normalizeStatus(request.status)) {
                        update.status = normalizeStatus(request.status);   // old name -> new name
                    }
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