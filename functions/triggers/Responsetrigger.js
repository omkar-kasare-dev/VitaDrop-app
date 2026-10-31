const admin = require("firebase-admin");

const {
    onDocumentWritten
} = require(
    "firebase-functions/v2/firestore"
);

const {
    notifyHospitalOfResponse
} = require(
    "../services/notificationService"
);

const {
    syncRequestWithResponses
} = require(
    "../services/responseService"
);

const {
    isOpen
} = require(
    "../utils/requestStatus"
);

const {
    RESPONSE,
    readResponse
} = require(
    "../utils/responseStatus"
);

/**
 * Runs whenever a donor's response is created or changed
 * (requests/{requestId}/responses/{donorId}):
 *  - a donor says "I can donate", declines, withdraws or changes their mind
 *  - the hospital records arrival and donation (through the setDonorOutcome function)
 *
 * STEP 4: the request is brought in line with ALL its responses (syncRequestWithResponses), so
 * the numbers can go down as well as up. A donor who withdraws, does not show up or is deferred
 * stops counting, and the search starts again if more donors are needed.
 *
 * The hospital gets ONE push for each donor who says yes. "hospitalNotified" is set before the
 * push is sent, so two events at the same moment cannot send it twice, and it is cleared again
 * if the push fails.
 */
exports.onResponseWritten =
    onDocumentWritten(
        "requests/{requestId}/responses/{donorId}",

        async (event) => {

            const {
                requestId,
                donorId
            } = event.params;

            await syncRequestWithResponses(requestId);

            const after = event.data.after;

            if (!after.exists) {
                return null;
            }

            const response = readResponse(after.data());

            if (response.responseStatus !== RESPONSE.ACCEPTED || after.get("hospitalNotified") === true) {
                return null;
            }

            const db = admin.firestore();

            const requestSnap =
                await db.collection("requests").doc(requestId).get();

            if (!requestSnap.exists || !isOpen(requestSnap.get("status"))) {
                return null;
            }

            // Claim the push first (transaction), so it is sent at most once
            const claimed = await db.runTransaction(async (tx) => {

                const fresh = await tx.get(after.ref);

                if (!fresh.exists || fresh.get("hospitalNotified") === true) {
                    return false;
                }

                tx.update(after.ref, { hospitalNotified: true });

                return true;
            });

            if (!claimed) {
                return null;
            }

            const request = { ...requestSnap.data(), requestId };

            const sent = await notifyHospitalOfResponse(
                request,
                donorId,
                Number(request.responseCount) || 1,
                Number(request.unitsRequired) || 1
            );

            if (!sent) {
                await after.ref.update({ hospitalNotified: false });
            }

            return null;
        }
    );