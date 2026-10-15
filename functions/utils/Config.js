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
    FCM_TTL_MS: 15 * 60 * 1000
};