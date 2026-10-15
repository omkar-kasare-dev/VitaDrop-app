/*
const {setGlobalOptions} = require("firebase-functions");
const {onRequest} = require("firebase-functions/https");
const logger = require("firebase-functions/logger");


const admin = require("firebase-admin");

admin.initializeApp();

const {
    onRequestCreated
} = require("./triggers/requestTrigger");

const {
    radiusExpansionScheduler
} = require("./schedulers/radiusExpansionScheduler");

exports.onRequestCreated =
    onRequestCreated;

exports.radiusExpansionScheduler =
    radiusExpansionScheduler;
    */


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

// 1) hospital creates a request   -> first alert (5 km)
exports.onRequestCreated =
    onRequestCreated;

// 2) donor taps "Respond Now"      -> counters, status, push to the hospital   (NEW)
exports.onResponseCreated =
    onResponseCreated;

// 3) nobody answered after 5 min   -> widen to 10 km, then 15 km
exports.radiusExpansionScheduler =
    radiusExpansionScheduler;

