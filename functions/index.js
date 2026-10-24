const admin = require("firebase-admin");

// Must run before the modules below are used
admin.initializeApp();

const {
    onRequestCreated
} = require("./triggers/requestTrigger");

const {
    onResponseCreated
} = require("./triggers/responseTrigger");

const {
    radiusExpansionScheduler
} = require("./schedulers/radiusExpansionScheduler");

const {
    syncDonorDirectory,
    syncHospitalStats,
    syncRequestStats
} = require("./triggers/directoryTriggers");

// 1) hospital creates a request   -> first alert (5 km)
exports.onRequestCreated =
    onRequestCreated;

// 2) donor taps "Respond Now"      -> counters, status, push to the hospital
exports.onResponseCreated =
    onResponseCreated;

// 3) nobody answered after 5 min   -> widen to 10 km, then 15 km
exports.radiusExpansionScheduler =
    radiusExpansionScheduler;

// 4) STEP 1: reduced donor view for hospitals + dashboard numbers
exports.syncDonorDirectory = syncDonorDirectory;
exports.syncHospitalStats = syncHospitalStats;
exports.syncRequestStats = syncRequestStats;