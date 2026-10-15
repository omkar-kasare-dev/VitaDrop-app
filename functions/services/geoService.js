const {
    calculateDistance
} = require("../utils/haversine");

/**
 * A usable location is a real latitude / longitude pair.
 * (0, 0) is what the Android app saves when GPS was not available, so it counts as "no location".
 * Before, (0, 0) was accepted: every donor looked thousands of km away and nobody was notified.
 */
function isValidLocation(lat, lng) {

    if (lat === null || lat === undefined || lng === null || lng === undefined) {
        return false;
    }

    const latitude = Number(lat);
    const longitude = Number(lng);

    if (Number.isNaN(latitude) || Number.isNaN(longitude)) {
        return false;
    }

    if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
        return false;
    }

    return !(latitude === 0 && longitude === 0);
}

function distanceKm(
    requestLat,
    requestLng,
    donorLat,
    donorLng
) {

    return calculateDistance(
        Number(requestLat),
        Number(requestLng),
        Number(donorLat),
        Number(donorLng)
    );
}

function isWithinRadius(
    requestLat,
    requestLng,
    donorLat,
    donorLng,
    radiusKm
) {

    return distanceKm(
        requestLat,
        requestLng,
        donorLat,
        donorLng
    ) <= radiusKm;
}

module.exports = {
    isValidLocation,
    distanceKm,
    isWithinRadius
};