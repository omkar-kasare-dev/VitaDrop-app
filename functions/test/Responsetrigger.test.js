// Offline test of onResponseCreated (no emulator, no network).
//   cd functions
//   node --test test/responseTrigger.test.js
const Module = require("node:module");
const test = require("node:test");
const assert = require("node:assert");

const DELETE = {};
const FieldValue = { arrayUnion: (...v) => ({ op: "union", v }) };

let store = {};
let pushResult = true;
const pushes = [];

function apply(doc, update) {
    for (const [k, v] of Object.entries(update)) {
        if (v && v.op === "union") doc[k] = [...new Set([...(doc[k] || []), ...v.v])];
        else doc[k] = v;
    }
}

const ref = (path) => ({
    path,
    id: path.split("/").pop(),
    get: async () => snap(path),
    update: async (u) => apply(store[path], u)
});
const snap = (path) => ({ exists: path in store, data: () => ({ ...store[path] }) });

const fakeDb = {
    collection: (name) => ({ doc: (id) => ref(`${name}/${id}`) }),
    runTransaction: async (fn) => fn({ get: async (r) => r.get(), update: (r, u) => apply(store[r.path], u) })
};

const original = Module._load;
Module._load = function (request, ...rest) {
    if (request === "firebase-admin") return { firestore: () => fakeDb };
    if (request === "firebase-admin/firestore") return { FieldValue };
    if (request === "firebase-functions/v2/firestore") return { onDocumentCreated: (path, handler) => handler };
    if (request === "../services/notificationService") {
        return {
            notifyHospitalOfResponse: async (req, donorId, count, units) => {
                pushes.push({ donorId, count, units });
                return pushResult;
            }
        };
    }
    return original.call(this, request, ...rest);
};

const { onResponseCreated: handler } = require("../triggers/responseTrigger");

const run = (donorId) => handler({
    params: { requestId: "R1", donorId },
    data: { ref: ref(`requests/R1/responses/${donorId}`) }
});

test.beforeEach(() => {
    store = {
        "requests/R1": { status: "pending", unitsRequired: 2, hospitalId: "H1", bloodGroup: "A+", acceptedDonors: [] },
        "requests/R1/responses/D1": { donorId: "D1", status: "accepted" },
        "requests/R1/responses/D2": { donorId: "D2", status: "accepted" }
    };
    pushes.length = 0;
    pushResult = true;
});

test("a response is counted once and the hospital is told", async () => {
    await run("D1");
    assert.strictEqual(store["requests/R1"].responseCount, 1);
    assert.deepStrictEqual(store["requests/R1"].acceptedDonors, ["D1"]);
    assert.strictEqual(store["requests/R1"].acceptedBy, "D1");
    assert.strictEqual(store["requests/R1"].status, "arranging");
    assert.strictEqual(store["requests/R1/responses/D1"].counted, true);
    assert.strictEqual(store["requests/R1/responses/D1"].hospitalNotified, true);
    assert.strictEqual(pushes.length, 1);
});

test("enough donors stops the search", async () => {
    await run("D1");
    await run("D2");
    assert.strictEqual(store["requests/R1"].responseCount, 2);
    assert.strictEqual(store["requests/R1"].status, "arranging");
});

test("a redelivered event neither recounts nor pushes again", async () => {
    await run("D1");
    await run("D1");
    assert.strictEqual(store["requests/R1"].responseCount, 1);
    assert.strictEqual(pushes.length, 1);
});

test("a push that was lost is sent again on redelivery, without recounting", async () => {
    pushResult = false;                       // hospital has no token / FCM failed
    await run("D1");
    assert.strictEqual(store["requests/R1/responses/D1"].hospitalNotified, undefined);
    assert.strictEqual(store["requests/R1"].responseCount, 1);

    pushResult = true;
    await run("D1");
    assert.strictEqual(store["requests/R1"].responseCount, 1, "still one");
    assert.strictEqual(store["requests/R1/responses/D1"].hospitalNotified, true);
    assert.strictEqual(pushes.length, 2);
});

test("a request that disappeared is ignored", async () => {
    delete store["requests/R1"];
    await run("D1");
    assert.strictEqual(pushes.length, 0);
});