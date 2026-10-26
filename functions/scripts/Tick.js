/**
 * Runs ONE scheduler pass by hand. The Functions emulator does not fire scheduled functions
 * by itself, so use this to test widening (5 -> 10 -> 15 km), retries and expiry.
 *
 * PowerShell, from the functions folder:
 *   $env:GCLOUD_PROJECT = "vitadrop-d851d"
 *   $env:FIRESTORE_EMULATOR_HOST = "127.0.0.1:8080"
 *   node scripts/tick.js
 *
 * Firestore is the emulator. Pushes are sent through the real FCM service, which needs Google
 * credentials (gcloud auth application-default login). Without them the log shows "failed",
 * but the claim / radius / expiry logic can still be checked in the Firestore UI.
 */
const admin = require("firebase-admin");

admin.initializeApp({ projectId: process.env.GCLOUD_PROJECT });

const { runExpansionTick } = require("../services/alertService");

runExpansionTick()
    .then((outcomes) => {
        console.log("tick done", JSON.stringify(outcomes));
        process.exit(0);
    })
    .catch((e) => {
        console.error(e);
        process.exit(1);
    });