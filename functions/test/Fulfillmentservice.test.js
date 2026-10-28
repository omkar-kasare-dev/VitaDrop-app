// Offline test of the hospital actions and the status rules (no emulator, no network).
//   cd functions
//   node --test test/fulfillmentService.test.js
const Module = require("node:module");
const test = require("node:test");
const assert = require("node:assert");

// ------------------------------------------------------------------ fake Firestore (by path)
class Ts { constructor(ms) { this.ms = ms; } toMillis() { return this.ms; } }
const DELETE = { op: "delete" };
const SERVER_TS = { op: "ts" };
const FieldValue = { serverTimestamp: () => SERVER_TS, delete: () => DELETE };
const Timestamp = { fromMillis: (ms) => new Ts(ms) };

let store = {};

function apply(doc, update) {
    for (const [k, v] of Object.entries(update)) {
        if (v === DELETE) delete doc[k];
        else if (v === SERVER_TS) doc[k] = new Ts(Date.now());
        else doc[k] = v;
    }
}

const snap = (path) => ({ exists: path in store, data: () => ({ ...store[path] }), get: (f) => (store[path] ? store[path][f] : undefined) });
const ref = (path) => ({ path, get: async () => snap(path) });

const fakeDb = {
    collection: (name) => ({ doc: (id) => ref(`${name}/${id}`) }),
    runTransaction: async (fn) => {
        const writes = [];
        const result = await fn({
            get: async (r) => snap(r.path),
            update: (r, u) => writes.push([r.path, u])
        });
        writes.forEach(([path, u]) => apply(store[path], u));    // all or nothing, like a transaction
        return result;
    }
};

class HttpsError extends Error { constructor(code, message) { super(message); this.code = code; } }

const original = Module._load;
Module._load = function (request, ...rest) {
    if (request === "firebase-admin") return { firestore: () => fakeDb };
    if (request === "firebase-admin/firestore") return { FieldValue, Timestamp };
    if (request === "firebase-functions/v2/https") return { onCall: (handler) => handler, HttpsError };
    return original.call(this, request, ...rest);
};

const { setUnitsFulfilled, cancelRequest } = require("../services/fulfillmentService");
const api = require("../api/requestActions");
const {
    normalizeStatus, isOpen, deriveStatus, expiryStatus, needsMoreDonors
} = require("../utils/requestStatus");
const { RESPONSE_CAP_FACTOR } = require("../utils/config");

test.beforeEach(() => {
    store = {
        "hospitals/H1": { isVerified: true, isBlocked: false },
        "hospitals/H2": { isVerified: true, isBlocked: false },
        "hospitals/H3": { isVerified: false, isBlocked: false },
        "requests/R1": { hospitalId: "H1", status: "arranging", unitsRequired: 3, unitsFulfilled: 0, responseCount: 2 }
    };
});

const fails = async (promise, code) => {
    await assert.rejects(promise, (e) => { assert.strictEqual(e.code, code, e.message); return true; });
};

// ------------------------------------------------------------------ status rules
test("old status names are mapped to the new ones", () => {
    assert.strictEqual(normalizeStatus("pending"), "searching");
    assert.strictEqual(normalizeStatus("accepted"), "arranging");
    assert.strictEqual(normalizeStatus("completed"), "fulfilled");
    assert.strictEqual(normalizeStatus("closed"), "unfulfilled");
    assert.strictEqual(normalizeStatus("partially_fulfilled"), "partially_fulfilled");
    assert.strictEqual(normalizeStatus(undefined), "searching");
    assert.strictEqual(normalizeStatus("something odd"), "searching");
});

test("which statuses are open", () => {
    for (const s of ["pending", "searching", "accepted", "arranging", "partially_fulfilled"]) assert.ok(isOpen(s), s);
    for (const s of ["fulfilled", "cancelled", "expired", "unfulfilled"]) assert.ok(!isOpen(s), s);
});

test("the status follows the confirmed units and the responses", () => {
    assert.strictEqual(deriveStatus({ unitsRequired: 2, unitsFulfilled: 0, responseCount: 0 }), "searching");
    assert.strictEqual(deriveStatus({ unitsRequired: 2, unitsFulfilled: 0, responseCount: 5 }), "arranging");
    assert.strictEqual(deriveStatus({ unitsRequired: 2, unitsFulfilled: 1, responseCount: 0 }), "partially_fulfilled");
    assert.strictEqual(deriveStatus({ unitsRequired: 2, unitsFulfilled: 2, responseCount: 0 }), "fulfilled");
});

test("closing by time: expired with nothing confirmed, unfulfilled with some units", () => {
    assert.strictEqual(expiryStatus({ unitsFulfilled: 0 }), "expired");
    assert.strictEqual(expiryStatus({ unitsFulfilled: 1 }), "unfulfilled");
});

test("the search stops at units x cap responses, or when all units are confirmed", () => {
    assert.ok(needsMoreDonors({ unitsRequired: 2, responseCount: 2 * RESPONSE_CAP_FACTOR - 1 }));
    assert.ok(!needsMoreDonors({ unitsRequired: 2, responseCount: 2 * RESPONSE_CAP_FACTOR }));
    assert.ok(!needsMoreDonors({ unitsRequired: 2, unitsFulfilled: 2, responseCount: 0 }));
});

// ------------------------------------------------------------------ confirming units
test("confirming some units makes the request partially fulfilled", async () => {
    const result = await setUnitsFulfilled({ requestId: "R1", units: 1, actorUid: "H1" });
    assert.deepStrictEqual(result, { status: "partially_fulfilled", unitsFulfilled: 1, unitsRequired: 3 });
    assert.strictEqual(store["requests/R1"].unitsFulfilled, 1);
    assert.strictEqual(store["requests/R1"].fulfillmentUpdatedBy, "H1");
});

test("confirming all units fulfils it and stops the alerts", async () => {
    store["requests/R1"].nextExpansionAt = new Ts(1);
    store["requests/R1"].claimUntil = new Ts(1);
    await setUnitsFulfilled({ requestId: "R1", units: 3, actorUid: "H1" });
    const r = store["requests/R1"];
    assert.strictEqual(r.status, "fulfilled");
    assert.ok(r.fulfilledAt);
    assert.ok(!r.nextExpansionAt && !r.claimUntil);
});

test("sending the same number twice changes nothing more", async () => {
    await setUnitsFulfilled({ requestId: "R1", units: 2, actorUid: "H1" });
    await setUnitsFulfilled({ requestId: "R1", units: 2, actorUid: "H1" });
    assert.strictEqual(store["requests/R1"].unitsFulfilled, 2);
});

test("lowering the number again re-opens a fulfilled request and restarts the alert clock", async () => {
    await setUnitsFulfilled({ requestId: "R1", units: 3, actorUid: "H1" });
    await setUnitsFulfilled({ requestId: "R1", units: 2, actorUid: "H1" });
    const r = store["requests/R1"];
    assert.strictEqual(r.status, "partially_fulfilled");
    assert.ok(!r.fulfilledAt);
    assert.ok(r.nextExpansionAt, "the scheduler can pick it up again");
});

test("going back to 0 returns to arranging (donors responded) or searching (nobody did)", async () => {
    await setUnitsFulfilled({ requestId: "R1", units: 1, actorUid: "H1" });
    await setUnitsFulfilled({ requestId: "R1", units: 0, actorUid: "H1" });
    assert.strictEqual(store["requests/R1"].status, "arranging");

    store["requests/R1"].responseCount = 0;
    await setUnitsFulfilled({ requestId: "R1", units: 1, actorUid: "H1" });
    await setUnitsFulfilled({ requestId: "R1", units: 0, actorUid: "H1" });
    assert.strictEqual(store["requests/R1"].status, "searching");
});

test("bad numbers are rejected", async () => {
    await fails(setUnitsFulfilled({ requestId: "R1", units: -1, actorUid: "H1" }), "invalid-argument");
    await fails(setUnitsFulfilled({ requestId: "R1", units: 1.5, actorUid: "H1" }), "invalid-argument");
    await fails(setUnitsFulfilled({ requestId: "R1", units: "two", actorUid: "H1" }), "invalid-argument");
    await fails(setUnitsFulfilled({ requestId: "R1", units: 4, actorUid: "H1" }), "invalid-argument");   // asked for 3
    assert.strictEqual(store["requests/R1"].unitsFulfilled, 0, "nothing was written");
});

test("only the hospital that owns the request can change it", async () => {
    await fails(setUnitsFulfilled({ requestId: "R1", units: 1, actorUid: "H2" }), "permission-denied");
    await fails(setUnitsFulfilled({ requestId: "R1", units: 1, actorUid: "D1" }), "permission-denied");
    store["requests/R3"] = { hospitalId: "H3", status: "searching", unitsRequired: 1 };
    await fails(setUnitsFulfilled({ requestId: "R3", units: 1, actorUid: "H3" }), "permission-denied");   // not verified
    store["hospitals/H1"].isBlocked = true;
    await fails(setUnitsFulfilled({ requestId: "R1", units: 1, actorUid: "H1" }), "permission-denied");
});

test("a closed request cannot be changed, and a missing one is reported", async () => {
    for (const status of ["cancelled", "expired", "unfulfilled"]) {
        store["requests/R1"].status = status;
        await fails(setUnitsFulfilled({ requestId: "R1", units: 1, actorUid: "H1" }), "failed-precondition");
    }
    await fails(setUnitsFulfilled({ requestId: "NOPE", units: 1, actorUid: "H1" }), "not-found");
});

test("the system (admin script) may change any request", async () => {
    await setUnitsFulfilled({ requestId: "R1", units: 1, actorUid: null });
    assert.strictEqual(store["requests/R1"].fulfillmentUpdatedBy, "system");
});

// ------------------------------------------------------------------ cancelling
test("the owner can cancel an open request", async () => {
    store["requests/R1"].nextExpansionAt = new Ts(1);
    await cancelRequest({ requestId: "R1", reason: "  Patient transferred  ", actorUid: "H1" });
    const r = store["requests/R1"];
    assert.strictEqual(r.status, "cancelled");
    assert.strictEqual(r.cancelReason, "Patient transferred");
    assert.strictEqual(r.cancelledBy, "H1");
    assert.ok(!r.nextExpansionAt);
});

test("cancelling needs the owner and an open request", async () => {
    await fails(cancelRequest({ requestId: "R1", actorUid: "H2" }), "permission-denied");
    store["requests/R1"].status = "fulfilled";
    await fails(cancelRequest({ requestId: "R1", actorUid: "H1" }), "failed-precondition");
});

// ------------------------------------------------------------------ the callable wrappers
test("the callable functions refuse a caller who is not signed in", async () => {
    await assert.rejects(api.setRequestUnits({ data: { requestId: "R1", units: 1 } }), (e) => e.code === "unauthenticated");
    await assert.rejects(api.cancelBloodRequest({ data: {} }), (e) => e.code === "unauthenticated");
});

test("the callable functions use the signed-in uid, never one sent in the data", async () => {
    await assert.rejects(
        api.setRequestUnits({ auth: { uid: "H2" }, data: { requestId: "R1", units: 1, actorUid: "H1", hospitalId: "H1" } }),
        (e) => e.code === "permission-denied"
    );
    const ok = await api.setRequestUnits({ auth: { uid: "H1" }, data: { requestId: "R1", units: 1 } });
    assert.strictEqual(ok.status, "partially_fulfilled");
});

test("a business error reaches the app as a readable message", async () => {
    await assert.rejects(
        api.setRequestUnits({ auth: { uid: "H1" }, data: { requestId: "R1", units: 9 } }),
        (e) => e.code === "invalid-argument" && /needs 3 unit/.test(e.message)
    );
});