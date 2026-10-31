const {
    onCall,
    HttpsError
} = require("firebase-functions/v2/https");

const {
    ActionError,
    setUnitsFulfilled,
    cancelRequest
} = require("../services/fulfillmentService");

const {
    setResponseOutcome
} = require("../services/responseService");

/**
 * Callable functions for the hospital app. The caller's identity comes from Firebase Auth
 * (request.auth), never from the data the app sends. Which hospital may change which request
 * is checked in fulfillmentService.
 */
function wrap(action) {

    return async (request) => {

        if (!request.auth) {
            throw new HttpsError("unauthenticated", "Please sign in.");
        }

        try {

            return await action(request.auth.uid, request.data || {});

        } catch (e) {

            if (e instanceof ActionError) {
                throw new HttpsError(e.code, e.message);
            }

            console.error("Request action failed:", e);

            throw new HttpsError("internal", "Something went wrong. Please try again.");
        }
    };
}

/** data: { requestId: string, units: number } */
exports.setRequestUnits =
    onCall(
        wrap((uid, data) =>
            setUnitsFulfilled({ requestId: data.requestId, units: data.units, actorUid: uid })
        )
    );

/** data: { requestId: string, reason?: string } */
exports.cancelBloodRequest =
    onCall(
        wrap((uid, data) =>
            cancelRequest({ requestId: data.requestId, reason: data.reason, actorUid: uid })
        )
    );

/**
 * data: { requestId, donorId, attendance?: "not_arrived" | "arrived" | "no_show",
 *                            donation?: "not_started" | "deferred" | "collected" }
 */
exports.setDonorOutcome =
    onCall(
        wrap((uid, data) =>
            setResponseOutcome({
                requestId: data.requestId,
                donorId: data.donorId,
                attendance: data.attendance,
                donation: data.donation,
                actorUid: uid
            })
        )
    );