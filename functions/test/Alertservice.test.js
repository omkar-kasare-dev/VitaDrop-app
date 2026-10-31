// Offline test of the claim / send / save logic (no emulator, no network).
//   cd functions
//   node --test test/alertService.test.js
const Module = require("node:module");
const test = require("node:test");
const assert = require("node:assert");

// ------------------------------------------------------------------ fake Firestore
class Ts { constructor(ms) { this.ms = ms; } toMillis() { return this.ms; } }

const DELETE = { op: "delete" };
const SERVER_TS = { op: "serverTimestamp" };

const FieldValue = {
    serverTimestamp: () => SERVER_TS,
    delete: () => DELETE,
    increment: (n) => ({ op: "increment", n }),
    arrayUnion: (...values) => ({ op: "union", values })
};
const Timestamp = { fromMillis: (ms) => new Ts(ms), now: () => new Ts(Date.now()) };

let docs = {};            // requests: id -> data
let failUpdates = 0;      // make the next N ref.update() calls fail
let queue = Promise.resolve();

function apply(doc, update) {
    for (const [key, value] of Object.entries(update)) {
        if (value === DELETE) delete doc[key];
        else if (value === SERVER_TS) doc[key] = new Ts(Date.now());
        else if (value && value.op === "increment") doc[key] = (doc[key] || 0) + value.n;
        else if (value && value.op === "union") doc[key] = [...new Set([...(doc[key] || []), ...value.values])];
        else doc[key] = value;
    }
}

const snap = (id) => ({
    id, exists: id in docs, ref: ref(id),
    data: () => ({ ...docs[id] }),
    get: (field) => (docs[id] ? docs[id][field] : undefined)
});

const ref = (id) => ({
    id,
    get: async () => snap(id),
    update: async (update) => {
        if (failUpdates > 0) { failUpdates--; throw new Error("simulated write failure"); }
        if (!(id in docs)) throw new Error("NOT_FOUND");
        apply(docs[id], update);
    }
});

const cmp = (a, b) => (a && a.toMillis ? a.toMillis() : a) - (b && b.toMillis ? b.toMillis() : b);
const ops = {
    "==": (a, b) => a === b,
    "<=": (a, b) => a !== undefined && cmp(a, b) <= 0,
    ">=": (a, b) => a !== undefined && cmp(a, b) >= 0,
    "in": (a, values) => Array.isArray(values) && values.includes(a)
};

const fakeDb = {
    collection: () => ({
        doc: (id) => ref(id),
        where(field, op, value) {
            const filters = [[field, op, value]];
            const q = {
                where(f, o, v) { filters.push([f, o, v]); return q; },
                limit() { return q; },
                async get() {
                    const list = Object.keys(docs)
                        .filter((id) => filters.every(([f, o, v]) => ops[o](docs[id][f], v)))
                        .map(snap);
                    return { docs: list, size: list.length };
                }
            };
            return q;
        }
    }),
    // transactions run one after the other, like Firestore serialises conflicting ones
    runTransaction(fn) {
        const tx = {
            get: async (r) => r.get(),
            update: (r, update) => { apply(docs[r.id], update); }
        };
        const run = queue.then(() => fn(tx));
        queue = run.catch(() => {});
        return run;
    }
};

// ------------------------------------------------------------------ stub the sender
const sent = [];
let notifyBehaviour = async (request, radius) => ({ notifiedDonorIds: ["D1"] });

const original = Module._load;
Module._load = function (request, ...rest) {
    if (request === "firebase-admin") return { firestore: () => fakeDb };
    if (request === "firebase-admin/firestore") return { FieldValue, Timestamp };
    if (request === "./notificationService") {
        return {
            notifyNearbyDonors: async (req, radius) => {
                sent.push({ id: req.requestId, radius });
                return notifyBehaviour(req, radius);
            }
        };
    }
    return original.call(this, request, ...rest);
};

const { processRequest, runExpansionTick } = require("../services/alertService");
const { RADIUS_STEPS_KM, EXPANSION_INTERVAL_MINUTES, REQUEST_EXPIRY_MINUTES, MAX_ALERT_ATTEMPTS } = require("../utils/config");

const MIN = 60 * 1000;
const ago = (minutes) => new Ts(Date.now() - minutes * MIN);
const inFuture = (minutes) => new Ts(Date.now() + minutes * MIN);

test.beforeEach(() => {
    docs = {};
    sent.length = 0;
    failUpdates = 0;
    queue = Promise.resolve();
    notifyBehaviour = async () => ({ notifiedDonorIds: ["D1"] });
});

const fresh = (extra = {}) => ({ status: "pending", createdAt: ago(1), unitsRequired: 2, notifiedDonors: [], ...extra });

// ------------------------------------------------------------------ tests
test("first alert goes to the first radius and the progress is saved", async () => {
    docs.R1 = fresh();
    assert.strictEqual(await processRequest(ref("R1")), "alerted");

    assert.deepStrictEqual(sent, [{ id: "R1", radius: RADIUS_STEPS_KM[0] }]);
    const d = docs.R1;
    assert.strictEqual(d.notificationRadius, RADIUS_STEPS_KM[0]);
    assert.ok(d.lastNotifiedAt, "lastNotifiedAt saved");
    assert.deepStrictEqual(d.notifiedDonors, ["D1"]);
    assert.ok(!d.claimUntil && !d.alertAttempts, "lease and attempts cleared");
    assert.ok(d.expiresAt, "expiresAt written");
    const wait = (d.nextExpansionAt.toMillis() - Date.now()) / MIN;
    assert.ok(wait > EXPANSION_INTERVAL_MINUTES - 0.1 && wait <= EXPANSION_INTERVAL_MINUTES, "next step in 5 min");
});

test("a duplicate delivery does not alert twice", async () => {
    docs.R1 = fresh();
    await processRequest(ref("R1"));
    assert.strictEqual(await processRequest(ref("R1")), "not_due");
    assert.strictEqual(sent.length, 1);
});

test("two runs at the same moment alert only once", async () => {
    docs.R1 = fresh();
    const outcomes = await Promise.all([processRequest(ref("R1")), processRequest(ref("R1"))]);
    assert.deepStrictEqual(outcomes.sort(), ["alerted", "leased"]);
    assert.strictEqual(sent.length, 1);
});

test("widening waits for the interval, then goes to the next radius", async () => {
    docs.R1 = fresh({ notificationRadius: 5, lastNotifiedAt: ago(1), nextExpansionAt: inFuture(4) });
    assert.strictEqual(await processRequest(ref("R1")), "not_due");
    assert.strictEqual(sent.length, 0);

    docs.R1.nextExpansionAt = ago(1);
    assert.strictEqual(await processRequest(ref("R1")), "alerted");
    assert.deepStrictEqual(sent, [{ id: "R1", radius: 10 }]);
    assert.strictEqual(docs.R1.notificationRadius, 10);
    assert.ok(docs.R1.nextExpansionAt, "another step is planned");
});

test("the last radius clears nextExpansionAt, and nothing is sent after it", async () => {
    docs.R1 = fresh({ notificationRadius: 10, lastNotifiedAt: ago(9), nextExpansionAt: ago(1) });
    assert.strictEqual(await processRequest(ref("R1")), "alerted");
    assert.strictEqual(docs.R1.notificationRadius, 15);
    assert.ok(!docs.R1.nextExpansionAt, "no further step");

    sent.length = 0;
    assert.strictEqual(await processRequest(ref("R1")), "max_radius");
    assert.strictEqual(sent.length, 0);
});

test("a request from the old scheduler (no nextExpansionAt) still widens", async () => {
    docs.R1 = fresh({ notificationRadius: 5, lastNotifiedAt: ago(6) });
    assert.strictEqual(await processRequest(ref("R1")), "alerted");
    assert.strictEqual(docs.R1.notificationRadius, 10);
});


test("legacy accepted requests can continue searching, while cancelled and completed requests are never alerted", async () => {
    docs.A = fresh({ status: "accepted" });
    docs.B = fresh({ status: "cancelled" });
    docs.C = fresh({ status: "completed" });

    assert.strictEqual(await processRequest(ref("A")), "alerted");
    assert.strictEqual(await processRequest(ref("B")), "inactive");
    assert.strictEqual(await processRequest(ref("C")), "inactive");

    assert.deepStrictEqual(sent, [
        { id: "A", radius: RADIUS_STEPS_KM[0] }
    ]);

    assert.strictEqual(docs.A.status, "arranging");
    assert.strictEqual(docs.B.status, "cancelled");
    assert.strictEqual(docs.C.status, "completed");
});


test("a request older than the expiry is closed and not alerted", async () => {
    docs.R1 = fresh({ createdAt: ago(REQUEST_EXPIRY_MINUTES + 5) });
    assert.strictEqual(await processRequest(ref("R1")), "expired");
    assert.strictEqual(docs.R1.status, "expired");
    assert.strictEqual(sent.length, 0);
});

test("an accepted request also expires", async () => {
    docs.R1 = fresh({ status: "accepted", createdAt: ago(REQUEST_EXPIRY_MINUTES + 5) });
    assert.strictEqual(await processRequest(ref("R1")), "expired");
    assert.strictEqual(docs.R1.status, "expired");
});

test("a failed send leaves a retry deadline and succeeds on the retry", async () => {
    docs.R1 = fresh();
    notifyBehaviour = async () => { throw new Error("firestore down"); };
    assert.strictEqual(await processRequest(ref("R1")), "failed");
    assert.strictEqual(docs.R1.alertAttempts, 1);
    assert.ok(docs.R1.nextExpansionAt, "scheduler can find it again");
    assert.ok(!docs.R1.lastNotifiedAt, "still counts as not alerted");

    // still inside the lease: nobody else sends
    assert.strictEqual(await processRequest(ref("R1")), "leased");

    // the lease ends, the retry works
    docs.R1.claimUntil = ago(1);
    notifyBehaviour = async () => ({ notifiedDonorIds: ["D1"] });
    assert.strictEqual(await processRequest(ref("R1")), "alerted");
    assert.ok(docs.R1.lastNotifiedAt);
    assert.ok(!docs.R1.alertAttempts);
});

test("after too many failures the request is parked with a reason", async () => {
    docs.R1 = fresh({ alertAttempts: MAX_ALERT_ATTEMPTS, claimUntil: ago(1) });
    assert.strictEqual(await processRequest(ref("R1")), "gave_up");
    assert.ok(docs.R1.alertError);
    assert.ok(!docs.R1.nextExpansionAt);
    assert.strictEqual(sent.length, 0);
});

test("a lost progress write is retried, and reported if it never works", async () => {
    docs.R1 = fresh();
    failUpdates = 1;                      // fails once, works on the second try
    assert.strictEqual(await processRequest(ref("R1")), "alerted");
    assert.ok(docs.R1.lastNotifiedAt);

    docs.R2 = fresh();
    failUpdates = 99;                     // never works
    assert.strictEqual(await processRequest(ref("R2")), "alerted_not_saved");
});

test("the scheduler run picks up due, stuck and expired requests and leaves the rest", async () => {
    docs.DUE = fresh({ notificationRadius: 5, lastNotifiedAt: ago(6), nextExpansionAt: ago(1) });
    docs.OLD = fresh({ createdAt: ago(REQUEST_EXPIRY_MINUTES + 5), lastNotifiedAt: ago(500), notificationRadius: 15 });
    docs.STUCK = fresh({ createdAt: ago(5) });                       // creation function never ran
    docs.NEW = fresh({ createdAt: ago(0.5) });                       // too young: its own trigger is still working
    docs.WAIT = fresh({ notificationRadius: 5, lastNotifiedAt: ago(1), nextExpansionAt: inFuture(4) });
    docs.LONGAGO = fresh({ createdAt: ago(120) });                   // outside the rescue window

    const outcomes = await runExpansionTick();

    assert.strictEqual(docs.DUE.notificationRadius, 10);
    assert.strictEqual(docs.OLD.status, "expired");
    assert.strictEqual(docs.STUCK.notificationRadius, RADIUS_STEPS_KM[0]);
    assert.ok(!docs.NEW.lastNotifiedAt, "young request untouched");
    assert.strictEqual(docs.WAIT.notificationRadius, 5, "not due, untouched");
    assert.ok(!docs.LONGAGO.lastNotifiedAt, "outside the window, untouched");
    assert.deepStrictEqual(sent.map((s) => s.id).sort(), ["DUE", "STUCK"]);
    assert.strictEqual(outcomes.alerted, 2);
    assert.strictEqual(outcomes.expired, 1);
});

test("a crashed first alert is retried by the scheduler", async () => {
    docs.R1 = fresh();
    notifyBehaviour = async () => { throw new Error("boom"); };
    await processRequest(ref("R1"));                // creation function fails
    docs.R1.claimUntil = ago(1);                    // lease over
    docs.R1.nextExpansionAt = ago(1);
    notifyBehaviour = async () => ({ notifiedDonorIds: ["D1"] });

    const outcomes = await runExpansionTick();
    assert.strictEqual(outcomes.alerted, 1);
    assert.strictEqual(docs.R1.notificationRadius, RADIUS_STEPS_KM[0]);
});