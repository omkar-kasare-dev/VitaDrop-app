const admin = require("firebase-admin");

const { FieldValue } = require("firebase-admin/firestore");

const {
    onDocumentWritten
} = require("firebase-functions/v2/firestore");

/**
 * Counts are RECOMPUTED (not incremented), so a retried event can never double-count.
 * Only runs when a document is created or deleted.
 */
async function refreshCount(collection, field) {

    const db = admin.firestore();

    const snap = await db.collection(collection).count().get();

    await db.collection("stats").doc("global").set(
        {
            [field]: snap.data().count,
            updatedAt: FieldValue.serverTimestamp()
        },
        { merge: true }
    );
}

function createdOrDeleted(event) {
    return event.data.before.exists !== event.data.after.exists;
}

/**
 * donors/{uid} -> donorDirectory/{uid}
 * The directory is the ONLY donor data a (verified) hospital can read:
 * no phone, email, age, weight, exact location or token.
 */
exports.syncDonorDirectory =
    onDocumentWritten(
        "donors/{uid}",

        async (event) => {

            const uid = event.params.uid;
            const db = admin.firestore();

            const before = event.data.before;
            const after = event.data.after;

            const ref = db.collection("donorDirectory").doc(uid);

            if (!after.exists || after.get("isBlocked") === true) {

                await ref.delete();

            } else {

                const view = {
                    fullName: after.get("fullName") || "",
                    bloodGroup: after.get("bloodGroup") || "",
                    city: after.get("city") || "",
                    state: after.get("state") || "",
                    gender: after.get("gender") || "",
                    isAvailable: after.get("isAvailable") !== false
                };

                const oldView = before.exists ? {
                    fullName: before.get("fullName") || "",
                    bloodGroup: before.get("bloodGroup") || "",
                    city: before.get("city") || "",
                    state: before.get("state") || "",
                    gender: before.get("gender") || "",
                    isAvailable: before.get("isAvailable") !== false
                } : null;

                // lastActive / token updates do not touch the directory
                if (JSON.stringify(view) !== JSON.stringify(oldView)) {
                    await ref.set({ ...view, updatedAt: FieldValue.serverTimestamp() });
                }
            }

            if (createdOrDeleted(event)) {
                await refreshCount("donors", "donorsCount");
            }

            return null;
        }
    );

exports.syncHospitalStats =
    onDocumentWritten(
        "hospitals/{uid}",

        async (event) => {

            if (createdOrDeleted(event)) {
                await refreshCount("hospitals", "hospitalsCount");
            }

            return null;
        }
    );

exports.syncRequestStats =
    onDocumentWritten(
        "requests/{requestId}",

        async (event) => {

            if (createdOrDeleted(event)) {
                await refreshCount("requests", "requestsCount");
            }

            return null;
        }
    );