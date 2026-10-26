const {
    onDocumentCreated
} = require(
    "firebase-functions/v2/firestore"
);

const {
    processRequest
} = require(
    "../services/alertService"
);

/**
 * Runs the moment a hospital creates a request (requests/{requestId}).
 * Sends the FIRST alert (5 km).
 *
 * STEP 2: the claim / send / save logic lives in alertService.processRequest, shared with the
 * scheduler. That fixes three problems of the old version:
 *  - a duplicate event no longer alerts everybody twice (the claim is a transaction)
 *  - a failure here no longer strands the request (the scheduler retries it)
 *  - a request cancelled a moment ago is not alerted (status is re-read inside the claim)
 */
exports.onRequestCreated =
    onDocumentCreated(
        "requests/{requestId}",

        async (event) => {

            const requestId =
                event.params.requestId;

            console.log(
                "New Request:",
                requestId
            );

            const outcome =
                await processRequest(event.data.ref);

            console.log(
                "onRequestCreated",
                requestId,
                outcome
            );

            return null;
        }
    );