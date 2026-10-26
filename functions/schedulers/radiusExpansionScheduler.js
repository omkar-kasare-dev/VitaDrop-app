const {
    onSchedule
} = require(
    "firebase-functions/v2/scheduler"
);

const {
    runExpansionTick
} = require(
    "../services/alertService"
);

/**
 * Runs every minute. STEP 2: it no longer reads every pending request every minute.
 * It only looks at requests that are due (see alertService.runExpansionTick):
 *  - the next radius step is due (5 -> 10 -> 15 km), or a crashed step must be retried
 *  - the request was never alerted (safety net)
 *  - the request is older than REQUEST_EXPIRY_MINUTES and is closed as "expired"
 * Every request is handled on its own, so one failure never stops the others.
 */
exports.radiusExpansionScheduler =
    onSchedule(

        {
            schedule: "* * * * *",     // every minute
            timeZone: "Asia/Kolkata",
            timeoutSeconds: 120
        },

        async () => {

            await runExpansionTick();

            return null;
        }
    );