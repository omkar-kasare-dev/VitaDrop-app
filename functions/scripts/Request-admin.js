/**
 * Change a request by hand while testing in the emulator (the hospital screen for this comes later).
 * It runs the same code as the callable functions, as the "system", so no login is needed.
 *
 * PowerShell, from the functions folder:
 *   $env:GCLOUD_PROJECT = "vitadrop-d851d"
 *   $env:FIRESTORE_EMULATOR_HOST = "127.0.0.1:8080"
 *   node scripts/request-admin.js units  <requestId> <number>
 *   node scripts/request-admin.js cancel <requestId> [reason]
 */
const admin = require("firebase-admin");

admin.initializeApp({ projectId: process.env.GCLOUD_PROJECT });

const { setUnitsFulfilled, cancelRequest } = require("../services/fulfillmentService");

const [command, requestId, value] = process.argv.slice(2);

async function main() {

    if (command === "units" && requestId && value !== undefined) {
        return setUnitsFulfilled({ requestId, units: value, actorUid: null });
    }

    if (command === "cancel" && requestId) {
        return cancelRequest({ requestId, reason: process.argv.slice(4).join(" "), actorUid: null });
    }

    throw new Error("Usage: node scripts/request-admin.js units <requestId> <number> | cancel <requestId> [reason]");
}

main()
    .then((result) => { console.log("done", JSON.stringify(result)); process.exit(0); })
    .catch((e) => { console.error(e.message || e); process.exit(1); });