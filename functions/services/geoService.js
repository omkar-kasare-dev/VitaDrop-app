const {
    calculateDistance
} = require("../utils/haversine");

function isWithinRadius(
    requestLat,
    requestLng,
    donorLat,
    donorLng,
    radiusKm
) {

    const distance =
        calculateDistance(
            requestLat,
            requestLng,
            donorLat,
            donorLng
        );

    return distance <= radiusKm;
}

module.exports = {
    isWithinRadius
};