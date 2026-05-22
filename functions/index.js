

const {setGlobalOptions} = require("firebase-functions");
const {onRequest} = require("firebase-functions/https");
const logger = require("firebase-functions/logger");


/*
const functions = require("firebase-functions");

exports.helloWorld = functions.https.onRequest((req, res) => {
  res.send("Hello from VitaDrop Firebase Functions 🚀");
});

//=====================================================
const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendPushNotification = functions.https.onRequest(async (req, res) => {
    try {

        const { token, title, body } = req.body;

        if (!token) {
            return res.status(400).send("Token missing");
        }

        const message = {
            token: token,
            notification: {
                title: title || "VitaDrop Alert",
                body: body || "Emergency notification"
            }
        };

        const response = await admin.messaging().send(message);

        return res.json({
            success: true,
            messageId: response
        });

    } catch (error) {
        return res.status(500).json({
            success: false,
            error: error.message
        });
    }
});
*/
//======================================
const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

const { onDocumentCreated } = require("firebase-functions/v2/firestore");


// ============================================
// EMERGENCY REQUEST TRIGGER (PRODUCTION)
// COLLECTION: requests
// ============================================
exports.onRequestCreated = onDocumentCreated(
    "requests/{requestId}",
    async (event) => {

        const data = event.data.data();

        console.log(" NEW REQUEST TRIGGERED");
        console.log("DATA:", data);

        // =========================
        // Extract your fields
        // =========================
        const bloodGroup = data.bloodGroup || "Unknown";
        const hospitalName = data.hospitalName || "Hospital";
        const unitsRequired = data.unitsRequired || "1";
        const city = data.city || "";
        const urgency = data.urgency || "normal";

        // =========================
        // Build notification
        // =========================
        const message = {
            topic: "donors",   // ALL DONORS SUBSCRIBED TO THIS TOPIC
            notification: {
                title: ` ${urgency.toUpperCase()} Emergency (${bloodGroup})`,
                body: `${unitsRequired} units needed at ${hospitalName}, ${city}`
            },
            data: {
                requestId: data.requestId || "",
                bloodGroup: bloodGroup,
                hospitalName: hospitalName,
                urgency: urgency
            }
        };

        try {
            const response = await admin.messaging().send(message);
            console.log("NOTIFICATION SENT:", response);
        } catch (error) {
            console.error(" ERROR SENDING NOTIFICATION:", error);
        }

        return null;
    }
);

exports.testPush = functions.https.onRequest(async (req, res) => {

    const response = await admin.messaging().send({
        topic: "donors",
        notification: {
            title: "🚨 TEST ALERT",
            body: "If you see this, system works"
        }
    });

    console.log("SUCCESS:", response);

    res.send("Notification Sent");
});

setGlobalOptions({ maxInstances: 10 });

