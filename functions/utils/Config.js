// One place for the numbers that control how fast and how far the alert spreads.

module.exports = {

    // Must be EXACTLY the same as NotificationHelper.CHANNEL_ID in the Android app.
    NOTIFICATION_CHANNEL_ID: "emergency_blood_requests",

    // First alert goes to donors inside 5 km. If nobody can help, it widens step by step.
    // (The system-flow picture shows 3 km: change the first number to 3 if you prefer that.)
    RADIUS_STEPS_KM: [5, 10, 15],

    // How long to wait before widening to the next radius.
    EXPANSION_INTERVAL_MINUTES: 5,

    // A donor who gave blood recently is not eligible (only used when lastDonationDate is stored).
    MIN_DAYS_BETWEEN_DONATIONS: 90,

    // If a phone is offline, an old emergency push is useless after this time.
    FCM_TTL_MS: 15 * 60 * 1000,

    // ---------------- STEP 2 ----------------

    // A request without its own deadline is closed after this long.
    REQUEST_EXPIRY_MINUTES: 24 * 60,

    // One alert step "owns" a request for this long. If the function crashes, another one
    // may retry after the lease. This is also what stops two runs alerting the same step twice.
    CLAIM_LEASE_SECONDS: 120,

    // After this many failed attempts at the same step the request is parked with an alertError.
    MAX_ALERT_ATTEMPTS: 5,

    // A request that was never alerted (the creation function did not run) is picked up by the
    // scheduler when it is older than RESCUE_AFTER_MINUTES, for up to RESCUE_WINDOW_MINUTES.
    RESCUE_AFTER_MINUTES: 2,
    RESCUE_WINDOW_MINUTES: 60,

    // Most requests handled per query in one scheduler run.
    SCHEDULER_BATCH_SIZE: 25,

    // ---------------- STEP 3 ----------------

    // The search keeps widening until the hospital has confirmed all units, OR until this many
    // times the units needed have said "I can donate". One tap can never end the search,
    // because donors may not show up or may be deferred at the centre.
    RESPONSE_CAP_FACTOR: 2
};