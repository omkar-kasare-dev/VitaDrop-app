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

const {
    setRequestUnits,
    cancelBloodRequest
} = require("./api/requestActions");

// 1) hospital creates a request   -> first alert (5 km)
exports.onRequestCreated =
    onRequestCreated;

// 2) donor taps "I can donate"     -> counts the response, tells the hospital
exports.onResponseCreated =
    onResponseCreated;

// 3) nobody answered after 5 min   -> widen to 10 km, then 15 km (also retries and expiry)
exports.radiusExpansionScheduler =
    radiusExpansionScheduler;

// 4) reduced donor view for hospitals + dashboard numbers
exports.syncDonorDirectory = syncDonorDirectory;
exports.syncHospitalStats = syncHospitalStats;
exports.syncRequestStats = syncRequestStats;

// 5) STEP 3: the hospital confirms units / cancels (callable from the app)
exports.setRequestUnits = setRequestUnits;
exports.cancelBloodRequest = cancelBloodRequest;