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

