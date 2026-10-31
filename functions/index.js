const admin = require("firebase-admin");

// Must run before the modules below are used
admin.initializeApp();

const {
    onRequestCreated
} = require("./triggers/requestTrigger");

const {
    onResponseWritten
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
    cancelBloodRequest,
    setDonorOutcome
} = require("./api/requestActions");

// 1) hospital creates a request   -> first alert (5 km)
exports.onRequestCreated =
    onRequestCreated;

// 2) a donor answers, changes their mind, or the hospital records arrival / donation
//    -> the request is brought in line with ALL its responses, the hospital is told once
exports.onResponseWritten =
    onResponseWritten;

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

// 6) STEP 4: the hospital records whether an accepted donor arrived and what happened
exports.setDonorOutcome = setDonorOutcome;