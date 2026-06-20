const {
    COMPATIBILITY
} = require("../utils/bloodCompatibility");

const admin =
    require("firebase-admin");

const {
    isWithinRadius
} = require("./geoService");

async function notifyNearbyDonors(
    requestData,
    radiusKm
) {

    const bloodGroup =
        requestData.bloodGroup;

    const requestLat =
        requestData.location?.latitude;

    const requestLng =
        requestData.location?.longitude;

    if (
        requestLat == null ||
        requestLng == null
    ) {

        console.log(
            "Request location missing"
        );

        return;
    }
/*
    const donorsSnapshot =
        await admin.firestore()
            .collection("donors")
            .where(
                "bloodGroup",
                "==",
                bloodGroup
            )
            .where(
                "isAvailable",
                "==",
                true
            )
            .get();
            */
            const compatibleGroups =
                COMPATIBILITY[bloodGroup] || [];
// Debug Logs
                console.log(
                    "Compatible Blood Groups:",
                    compatibleGroups
                );
//End
            const donorsSnapshot =
                await admin.firestore()
                    .collection("donors")
                    .where(
                        "isAvailable",
                        "==",
                        true
                    )
                    .where(
                        "bloodGroup",
                        "in",
                        compatibleGroups
                    )
                    .get();

    let notifiedCount = 0;

    for (const doc of donorsSnapshot.docs) {

        const donor =
            doc.data();

        const donorLat =
            donor.location?.latitude;

        const donorLng =
            donor.location?.longitude;

        const token =
            donor.fcmToken;

        if (
            donorLat == null ||
            donorLng == null ||
            !token
        ) {
            continue;
        }

        const nearby =
            isWithinRadius(
                requestLat,
                requestLng,
                donorLat,
                donorLng,
                radiusKm
            );

        if (!nearby) {
            continue;
        }

        try {

            await admin
                .messaging()
                .send({

                    token: token,

                    notification: {

                        title:
                            ` ${bloodGroup} Blood Needed`,

                        body:
                            `${requestData.hospitalName} requires blood urgently`
                    },

                    data: {

                        requestId:
                            requestData.requestId || "",

                        bloodGroup,

                        radius:
                            radiusKm.toString()
                    }
                });

            notifiedCount++;

        } catch (e) {

            console.error(
                "FCM Error:",
                e
            );
        }
    }

    console.log(
        `Radius ${radiusKm} KM -> Notified ${notifiedCount} donors`
    );
}

module.exports = {
    notifyNearbyDonors
};