const admin = require("firebase-admin");

const READ_CHUNK = 300;
const DELETE_CHUNK = 400;

/**
 * Push tokens live in devices/{uid} (readable only by Cloud Functions, never by clients).
 * Returns a Map<uid, token> for the users that have a token.
 */
async function getTokens(uids) {

    const db = admin.firestore();
    const tokens = new Map();

    for (let i = 0; i < uids.length; i += READ_CHUNK) {

        const refs = uids
            .slice(i, i + READ_CHUNK)
            .map((id) => db.collection("devices").doc(id));

        const docs = await db.getAll(...refs);

        docs.forEach((d) => {
            const token = d.exists ? d.get("token") : null;
            if (token) {
                tokens.set(d.id, token);
            }
        });
    }

    return tokens;
}

/** Dead tokens (reported by FCM) are deleted so they are not tried again. */
async function removeTokens(uids) {

    const db = admin.firestore();

    for (let i = 0; i < uids.length; i += DELETE_CHUNK) {

        const batch = db.batch();

        uids.slice(i, i + DELETE_CHUNK).forEach((id) => {
            batch.delete(db.collection("devices").doc(id));
        });

        await batch.commit();
    }
}

module.exports = { getTokens, removeTokens };