// Offline test of the response numbers and the hospital's attendance / donation records.
//   cd functions
//   node --test test/responseService.test.js
const Module = require("node:module");
const test = require("node:test");
const assert = require("node:assert");

const { createFakeDb, Ts, FieldValue, Timestamp } = require("./helpers/fakeDb");
const fake = createFakeDb();
const store = () => fake.state.store;

const original = Module._load;
Module._load = function (request, ...rest) {
    if (request === "firebase-admin") return { firestore: () => fake.db };
    if (request === "firebase-admin/firestore") return { FieldValue, Timestamp };
    return original.call(this, request, ...rest);
};

const { syncRequestWithResponses, setResponseOutcome } = require("../services/responseService");

const MIN = 60 * 1000;
const ago = (minutes) => new Ts(Date.now() - minutes * MIN);
const accepted = (extra = {}) => ({ responseStatus: "accepted", attendanceStatus: "not_arrived", donationStatus: "not_started", ...extra });

test.beforeEach(() => {
    fake.state.store = {
        "hospitals/H1": { isVerified: true, isBlocked: false },
        "hospitals/H2": { isVerified: true, isBlocked: false },
        "hospitals/H3": { isVerified: false, isBlocked: false },
        "donors/D1": { lastDonationDate: new Ts(Date.parse("2026-01-10")) },
        "donors/D2": {},
        "donors/D3": {},
        "requests/R1": {
            hospitalId: "H1", status: "arranging", unitsRequired: 2, unitsFulfilled: 0,
            responseCount: 0, notificationRadius: 5, lastNotifiedAt: ago(20), nextExpansionAt: ago(1)
        },
        "requests/R1/responses/D1": accepted(),
        "requests/R1/responses/D2": accepted(),
        "requests/R1/responses/D3": { responseStatus: "declined", attendanceStatus: "not_arrived", donationStatus: "not_started" }
    };
    fake.state.txWrites = 0;
});

const fails = async (promise, code) => {
    await assert.rejects(promise, (e) => { assert.strictEqual(e.code, code, e.message); return true; });
};

// ------------------------------------------------------------------ syncRequestWithResponses
test("only donors who are still expected are counted", async () => {
    store()["requests/R1/responses/D2"] = accepted({ attendanceStatus: "no_show" });
    await syncRequestWithResponses("R1");
    const r = store()["requests/R1"];
    assert.strictEqual(r.responseCount, 1);
    assert.deepStrictEqual(r.acceptedDonors, ["D1"]);
    assert.deepStrictEqual(r.rejectedDonors, ["D3"]);
    assert.deepStrictEqual(r.responseSummary, {
        accepted: 2, declined: 1, withdrawn: 0, active: 1, arrived: 0, noShow: 1, deferred: 0, collected: 0
    });
    assert.strictEqual(r.status, "arranging");
});

test("the numbers can go DOWN: everybody drops out and the request goes back to searching", async () => {
    await syncRequestWithResponses("R1");
    assert.strictEqual(store()["requests/R1"].responseCount, 2);

    store()["requests/R1/responses/D1"] = accepted({ responseStatus: "withdrawn" });
    store()["requests/R1/responses/D2"] = accepted({ responseStatus: "declined" });
    await syncRequestWithResponses("R1");
    const r = store()["requests/R1"];
    assert.strictEqual(r.responseCount, 0);
    assert.strictEqual(r.status, "searching");
    assert.deepStrictEqual(r.rejectedDonors.sort(), ["D1", "D2", "D3"]);
});

test("a response written before Step 4 counts as accepted", async () => {
    store()["requests/R1/responses/D1"] = { status: "accepted", responseTime: 123 };
    await syncRequestWithResponses("R1");
    assert.ok(store()["requests/R1"].acceptedDonors.includes("D1"));
});

test("running it twice changes nothing the second time", async () => {
    await syncRequestWithResponses("R1");
    const writes = fake.state.txWrites;
    await syncRequestWithResponses("R1");
    assert.strictEqual(fake.state.txWrites, writes);
});

test("a partly fulfilled request is not set back to arranging when donors drop out", async () => {
    store()["requests/R1"].status = "partially_fulfilled";
    store()["requests/R1"].unitsFulfilled = 1;
    store()["requests/R1/responses/D1"] = accepted({ responseStatus: "withdrawn" });
    store()["requests/R1/responses/D2"] = accepted({ responseStatus: "withdrawn" });
    await syncRequestWithResponses("R1");
    assert.strictEqual(store()["requests/R1"].status, "partially_fulfilled");
});

test("a finished request keeps its status but its numbers are still updated", async () => {
    store()["requests/R1"].status = "fulfilled";
    await syncRequestWithResponses("R1");
    assert.strictEqual(store()["requests/R1"].status, "fulfilled");
    assert.strictEqual(store()["requests/R1"].responseCount, 2);
});

test("a request that does not exist is ignored", async () => {
    assert.strictEqual(await syncRequestWithResponses("NOPE"), null);
});

test("when donors drop out below the cap, the search that had stopped starts again", async () => {
    const r = store()["requests/R1"];
    r.unitsRequired = 1;                 // cap = 2 responses
    r.responseCount = 2;
    delete r.nextExpansionAt;            // the search had stopped
    store()["requests/R1/responses/D2"] = accepted({ responseStatus: "withdrawn" });
    await syncRequestWithResponses("R1");
    assert.ok(store()["requests/R1"].nextExpansionAt, "the scheduler can widen again");
});

test("it does not restart a search that has nowhere left to widen, or one that gave up", async () => {
    const r = store()["requests/R1"];
    r.unitsRequired = 1;
    delete r.nextExpansionAt;
    r.notificationRadius = 15;
    await syncRequestWithResponses("R1");
    assert.ok(!store()["requests/R1"].nextExpansionAt, "largest radius already reached");

    r.notificationRadius = 5;
    r.alertError = "Gave up";
    await syncRequestWithResponses("R1");
    assert.ok(!store()["requests/R1"].nextExpansionAt, "parked requests stay parked");
});

// ------------------------------------------------------------------ setResponseOutcome
test("the hospital marks a donor as arrived", async () => {
    const out = await setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", actorUid: "H1" });
    assert.deepStrictEqual(out, { attendanceStatus: "arrived", donationStatus: "not_started" });
    const d = store()["requests/R1/responses/D1"];
    assert.strictEqual(d.attendanceStatus, "arrived");
    assert.strictEqual(d.outcomeUpdatedBy, "H1");
    assert.strictEqual(store()["requests/R1"].responseSummary.arrived, 1);
});

test("a donation can only be recorded for a donor who arrived", async () => {
    await fails(setResponseOutcome({ requestId: "R1", donorId: "D1", donation: "collected", actorUid: "H1" }), "invalid-argument");
    await fails(setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "no_show", donation: "collected", actorUid: "H1" }), "invalid-argument");
    assert.strictEqual(store()["requests/R1/responses/D1"].donationStatus, "not_started");
});

test("collected records the donation date, and never touches the confirmed units", async () => {
    await setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", actorUid: "H1" });
    const before = Date.now();
    await setResponseOutcome({ requestId: "R1", donorId: "D1", donation: "collected", actorUid: "H1" });

    assert.ok(store()["donors/D1"].lastDonationDate.toMillis() >= before, "donor cooldown starts now");
    assert.strictEqual(store()["requests/R1/responses/D1"].priorLastDonationDate.toMillis(), Date.parse("2026-01-10"));
    assert.strictEqual(store()["requests/R1"].responseSummary.collected, 1);
    assert.strictEqual(store()["requests/R1"].unitsFulfilled, 0, "a collected donation is not a unit for the patient");
});

test("undoing a collected donation restores the donor's previous date", async () => {
    await setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", donation: "collected", actorUid: "H1" });
    await setResponseOutcome({ requestId: "R1", donorId: "D1", donation: "not_started", actorUid: "H1" });
    assert.strictEqual(store()["donors/D1"].lastDonationDate.toMillis(), Date.parse("2026-01-10"));
    assert.ok(!("collectedAt" in store()["requests/R1/responses/D1"]));
});

test("a donor who had never donated gets no date back after an undo", async () => {
    await setResponseOutcome({ requestId: "R1", donorId: "D2", attendance: "arrived", donation: "collected", actorUid: "H1" });
    assert.ok(store()["donors/D2"].lastDonationDate);
    await setResponseOutcome({ requestId: "R1", donorId: "D2", donation: "not_started", actorUid: "H1" });
    assert.strictEqual(store()["donors/D2"].lastDonationDate, null);
});

test("an undo never overwrites a newer donation date", async () => {
    await setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", donation: "collected", actorUid: "H1" });
    store()["donors/D1"].lastDonationDate = new Ts(Date.now() + 5 * MIN);      // a later donation elsewhere
    const newer = store()["donors/D1"].lastDonationDate.toMillis();
    await setResponseOutcome({ requestId: "R1", donorId: "D1", donation: "not_started", actorUid: "H1" });
    assert.strictEqual(store()["donors/D1"].lastDonationDate.toMillis(), newer);
});

test("changing a donor to no-show clears the donation and stops counting them", async () => {
    await setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", donation: "collected", actorUid: "H1" });
    await setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "no_show", actorUid: "H1" });
    const d = store()["requests/R1/responses/D1"];
    assert.strictEqual(d.donationStatus, "not_started");
    assert.strictEqual(store()["donors/D1"].lastDonationDate.toMillis(), Date.parse("2026-01-10"));
    assert.strictEqual(store()["requests/R1"].responseCount, 1);
});

test("a deferred donor is not counted as someone we can expect", async () => {
    await setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", donation: "deferred", actorUid: "H1" });
    const r = store()["requests/R1"];
    assert.strictEqual(r.responseCount, 1);
    assert.strictEqual(r.responseSummary.deferred, 1);
    assert.strictEqual(store()["donors/D1"].lastDonationDate.toMillis(), Date.parse("2026-01-10"), "no donation, no cooldown");
});

test("only a donor who said yes can be marked", async () => {
    await fails(setResponseOutcome({ requestId: "R1", donorId: "D3", attendance: "arrived", actorUid: "H1" }), "failed-precondition");
    await fails(setResponseOutcome({ requestId: "R1", donorId: "NOBODY", attendance: "arrived", actorUid: "H1" }), "not-found");
    await fails(setResponseOutcome({ requestId: "NOPE", donorId: "D1", attendance: "arrived", actorUid: "H1" }), "not-found");
});

test("only the owning, verified hospital can record outcomes", async () => {
    await fails(setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", actorUid: "H2" }), "permission-denied");
    await fails(setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", actorUid: "D1" }), "permission-denied");
    store()["requests/R3"] = { hospitalId: "H3", status: "searching", unitsRequired: 1 };
    store()["requests/R3/responses/D1"] = accepted();
    await fails(setResponseOutcome({ requestId: "R3", donorId: "D1", attendance: "arrived", actorUid: "H3" }), "permission-denied");
    assert.strictEqual(store()["requests/R1/responses/D1"].attendanceStatus, "not_arrived");
});

test("bad values are rejected, and the system (admin script) may record outcomes", async () => {
    await fails(setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "teleported", actorUid: "H1" }), "invalid-argument");
    await fails(setResponseOutcome({ requestId: "R1", donorId: "D1", donation: "sold", actorUid: "H1" }), "invalid-argument");
    await fails(setResponseOutcome({ requestId: "R1", donorId: "D1", actorUid: "H1" }), "invalid-argument");
    await setResponseOutcome({ requestId: "R1", donorId: "D1", attendance: "arrived", actorUid: null });
    assert.strictEqual(store()["requests/R1/responses/D1"].outcomeUpdatedBy, "system");
});