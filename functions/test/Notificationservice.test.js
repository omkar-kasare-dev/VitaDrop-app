// Offline test of donor matching and the pushes (no emulator, no network).
//   cd functions
//   node --test test/notificationService.test.js
const Module = require("node:module");
const test = require("node:test");
const assert = require("node:assert");

let store;
const sent = [];
const messages = [];

const snap = (col, id) => { const d = store[col]?.[id]; return { id, exists: !!d, data: () => d, get: (f) => (d ? d[f] : undefined) }; };
const ref = (col, id) => ({ id, _col: col, get: async () => snap(col, id) });

const fakeDb = {
    collection: (col) => ({
        doc: (id) => ref(col, id),
        where(f, op, v) {
            const filters = [[f, op, v]];
            const q = {
                where(f2, o2, v2) { filters.push([f2, o2, v2]); return q; },
                async get() {
                    const list = Object.keys(store[col])
                        .filter((id) => filters.every(([ff, oo, vv]) => (oo === "==" ? store[col][id][ff] === vv : vv.includes(store[col][id][ff]))))
                        .map((id) => snap(col, id));
                    return { docs: list, size: list.length, empty: list.length === 0 };
                }
            };
            return q;
        }
    }),
    getAll: async (...refs) => refs.map((r) => snap(r._col, r.id)),
    batch: () => { const ops = []; return { delete: (r) => ops.push(r), commit: async () => ops.forEach((r) => delete store[r._col][r.id]) }; }
};

const fakeAdmin = {
    firestore: () => fakeDb,
    messaging: () => ({
        sendEachForMulticast: async (m) => {
            sent.push(...m.tokens);
            return { responses: m.tokens.map((t) => (t === "dead-token" ? { success: false, error: { code: "messaging/registration-token-not-registered" } } : { success: true })) };
        },
        send: async (m) => { sent.push(m.token); messages.push(m); }
    })
};

const original = Module._load;
Module._load = function (request, ...rest) {
    return request === "firebase-admin" ? fakeAdmin : original.call(this, request, ...rest);
};

const { notifyNearbyDonors, notifyHospitalOfResponse } = require("../services/notificationService");

const REQUEST = {
    requestId: "R1", bloodGroup: "A+", city: "Pune", hospitalName: "Kem", unitsRequired: 2,
    hospitalId: "H1", patientName: "P", location: { latitude: 18.5080991, longitude: 73.9555957 }, notifiedDonors: []
};

test.beforeEach(() => {
    sent.length = 0;
    messages.length = 0;
    store = {
        donors: {
            D1: { isAvailable: true, bloodGroup: "O+", city: "Pune", location: { latitude: 18.5081, longitude: 73.9555 }, fullName: "Raju" },
            D2: { isAvailable: true, bloodGroup: "O+", city: "Pune", location: { latitude: 18.5082, longitude: 73.9556 }, fullName: "NoToken" },
            D3: { isAvailable: true, bloodGroup: "A+", city: "Pune", location: { latitude: 18.5083, longitude: 73.9557 }, fullName: "DeadToken" },
            D4: { isAvailable: true, bloodGroup: "O+", city: "Delhi", location: { latitude: 28.6, longitude: 77.2 }, fullName: "FarAway" },
            D5: { isAvailable: true, bloodGroup: "O+", city: "Pune", fullName: "SameCityNoGps" }
        },
        devices: { D1: { token: "tok-D1" }, D3: { token: "dead-token" }, D4: { token: "tok-D4" }, D5: { token: "tok-D5" }, H1: { token: "tok-H1" } }
    };
});

test("only a nearby, compatible donor with a token is alerted", async () => {
    const { notifiedDonorIds, summary } = await notifyNearbyDonors(REQUEST, 5);
    assert.deepStrictEqual(notifiedDonorIds, ["D1"]);
    assert.strictEqual(summary.skippedNoToken, 1);
    assert.deepStrictEqual(summary.noTokenDonorIds, ["D2"]);
    assert.strictEqual(summary.skippedTooFar, 1);          // D4
    assert.strictEqual(summary.removedDeadTokens, 1);      // D3
    assert.ok(!store.devices.D3 && store.devices.D1);
});

test("same city is NOT enough any more: a donor without GPS is skipped", async () => {
    const { summary } = await notifyNearbyDonors(REQUEST, 5);
    assert.strictEqual(summary.skippedNoLocation, 1);      // D5
    assert.ok(!sent.includes("tok-D5"));
});

test("a request without a real location alerts nobody", async () => {
    const { notifiedDonorIds } = await notifyNearbyDonors({ ...REQUEST, location: { latitude: 0, longitude: 0 } }, 5);
    assert.deepStrictEqual(notifiedDonorIds, []);
    assert.strictEqual(sent.length, 0);
});

test("already notified donors are not alerted again", async () => {
    const { notifiedDonorIds, summary } = await notifyNearbyDonors({ ...REQUEST, notifiedDonors: ["D1"] }, 5);
    assert.deepStrictEqual(notifiedDonorIds, []);
    assert.strictEqual(summary.skippedAlreadyNotified, 1);
});

test("the hospital push counts donors, not units, and reports success", async () => {
    assert.strictEqual(await notifyHospitalOfResponse(REQUEST, "D1", 1, 2), true);
    assert.strictEqual(messages[0].notification.title, "1 donor responded");
    assert.match(messages[0].notification.body, /Raju can donate A\+/);

    assert.strictEqual(await notifyHospitalOfResponse(REQUEST, "D1", 2, 2), true);
    assert.strictEqual(messages[1].notification.title, "2 donors responded");
});

test("a hospital without a token returns false and does not crash", async () => {
    delete store.devices.H1;
    assert.strictEqual(await notifyHospitalOfResponse(REQUEST, "D1", 1, 2), false);
    assert.strictEqual(sent.length, 0);
});