/**
 * One-time backfill for data that existed BEFORE the new rules/functions:
 *   - donorDirectory/{uid}   from donors
 *   - stats/global           (3 counts)
 *   - devices/{uid}          from the old fcmToken field on donors / hospitals
 *
 * Emulator:
 *   cd functions
 *   GCLOUD_PROJECT=<your-project-id> FIRESTORE_EMULATOR_HOST=127.0.0.1:8080 node scripts/backfill.js
 */
const admin = require("firebase-admin");
const { FieldValue } = require("firebase-admin/firestore");

admin.initializeApp({ projectId: process.env.GCLOUD_PROJECT });
const db = admin.firestore();

async function run() {

    const donors = await db.collection("donors").get();
    const hospitals = await db.collection("hospitals").get();
    const requests = await db.collection("requests").count().get();

    for (const doc of donors.docs) {
        const d = doc.data();

        if (d.isBlocked !== true) {
            await db.collection("donorDirectory").doc(doc.id).set({
                fullName: d.fullName || "",
                bloodGroup: d.bloodGroup || "",
                city: d.city || "",
                state: d.state || "",
                gender: d.gender || "",
                isAvailable: d.isAvailable !== false,
                updatedAt: FieldValue.serverTimestamp()
            });
        }

        if (d.fcmToken) {
            await db.collection("devices").doc(doc.id).set({
                token: d.fcmToken, platform: "android", updatedAt: FieldValue.serverTimestamp()
            });
        }
    }

    for (const doc of hospitals.docs) {
        const token = doc.get("fcmToken");
        if (token) {
            await db.collection("devices").doc(doc.id).set({
                token, platform: "android", updatedAt: FieldValue.serverTimestamp()
            });
        }
    }

    await db.collection("stats").doc("global").set({
        donorsCount: donors.size,
        hospitalsCount: hospitals.size,
        requestsCount: requests.data().count,
        updatedAt: FieldValue.serverTimestamp()
    }, { merge: true });

    console.log(`Done: ${donors.size} donors, ${hospitals.size} hospitals, ${requests.data().count} requests`);
}

run().catch((e) => { console.error(e); process.exit(1); });