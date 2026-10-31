// Run from the project root (where firebase.json is):
//   cd rules-test && npm install && cd ..
//   firebase emulators:exec --only firestore "node --test rules-test/rules.test.js"
const { it, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");
const {
    initializeTestEnvironment, assertFails, assertSucceeds
} = require("@firebase/rules-unit-testing");
const {
    doc, getDoc, getDocs, collection, setDoc, updateDoc, deleteDoc, serverTimestamp, Timestamp
} = require("firebase/firestore");

let env;
const db = (uid) => env.authenticatedContext(uid).firestore();

const donorProfile = (uid, over = {}) => ({
    uid, fullName: "Test Donor", email: "d@test.com", phone: "9000000000", gender: "Male", age: 30,
    bloodGroup: "O+", city: "Pune", state: "MH", address: "x",
    location: { latitude: 18.5, longitude: 73.9 }, profileImage: "", weight: 70,
    lastDonationDate: null, isAvailable: true, isVerified: false, isBlocked: false,
    devicePlatform: "android", createdAt: Timestamp.now(), updatedAt: Timestamp.now(),
    lastActive: Timestamp.now(), ...over
});

const newRequest = (id, hospitalId, over = {}) => ({
    requestId: id, requestedBy: "hospital", hospitalId, patientName: "P", bloodGroup: "A+",
    unitsRequired: 2, contactPerson: "C", contactPhone: "9000000001", hospitalName: "Kem",
    city: "Pune", location: { latitude: 18.5, longitude: 73.9 }, urgency: "high", status: "searching",
    bloodComponent: "RED_CELLS", requiredWithinHours: 6, patientBloodGroupVerified: false,
    unitsFulfilled: 0, responseCount: 0,
    description: "", acceptedDonors: [], rejectedDonors: [], completedBy: [], notificationRadius: 5,
    acceptedBy: "", notifiedDonors: [], notificationStartedAt: serverTimestamp(), emergency: true,
    createdBy: hospitalId, createdAt: serverTimestamp(), ...over
});

before(async () => {
    env = await initializeTestEnvironment({
        projectId: "vitadrop-rules-test",
        firestore: { rules: fs.readFileSync("firestore.rules", "utf8") }
    });
});

after(async () => { await env.cleanup(); });

beforeEach(async () => {
    await env.clearFirestore();
    await env.withSecurityRulesDisabled(async (ctx) => {
        const f = ctx.firestore();
        await setDoc(doc(f, "hospitals/H1"), { hospitalName: "Kem", isVerified: true, isBlocked: false });
        await setDoc(doc(f, "hospitals/H2"), { hospitalName: "Other", isVerified: false, isBlocked: false });
        await setDoc(doc(f, "donors/D1"), donorProfile("D1"));
        await setDoc(doc(f, "donors/D2"), donorProfile("D2"));
        await setDoc(doc(f, "requests/R1"), { hospitalId: "H1", status: "searching", notifiedDonors: ["D1"] });
        await setDoc(doc(f, "requests/RF"), { hospitalId: "H1", status: "fulfilled", notifiedDonors: ["D1"] });
        await setDoc(doc(f, "requests/RA"), { hospitalId: "H1", status: "arranging", notifiedDonors: ["D1"] });
        await setDoc(doc(f, "requests/RU"), { hospitalId: "H1", status: "arranging", notifiedDonors: ["D1", "D2"] });
        await setDoc(doc(f, "requests/RU/responses/D1"), { donorId: "D1", requestId: "RU", responseStatus: "accepted", attendanceStatus: "not_arrived", donationStatus: "not_started" });
        await setDoc(doc(f, "requests/RU/responses/D2"), { donorId: "D2", requestId: "RU", responseStatus: "accepted", attendanceStatus: "arrived", donationStatus: "not_started" });
        await setDoc(doc(f, "requests/RL"), { hospitalId: "H1", status: "searching", notifiedDonors: ["D1"] });
        await setDoc(doc(f, "requests/RL/responses/D1"), { donorId: "D1", requestId: "RL", status: "accepted", responseTime: 1 });
        await setDoc(doc(f, "donorDirectory/D1"), { fullName: "Test Donor", bloodGroup: "O+" });
        await setDoc(doc(f, "stats/global"), { donorsCount: 2 });
    });
});

// ---------- profiles ----------
it("a donor cannot read another donor", () =>
    assertFails(getDoc(doc(db("D2"), "donors/D1"))));

it("a donor can read their own profile", () =>
    assertSucceeds(getDoc(doc(db("D1"), "donors/D1"))));

it("a hospital cannot read donor profiles", () =>
    assertFails(getDoc(doc(db("H1"), "donors/D1"))));

it("registration: valid donor profile is accepted", () =>
    assertSucceeds(setDoc(doc(db("NEW1"), "donors/NEW1"), donorProfile("NEW1"))));

it("registration: a self-verified donor is rejected", () =>
    assertFails(setDoc(doc(db("NEW1"), "donors/NEW1"), donorProfile("NEW1", { isVerified: true }))));

it("one account cannot be donor AND hospital", () =>
    assertFails(setDoc(doc(db("H1"), "donors/H1"), donorProfile("H1"))));

it("a hospital cannot verify itself", () =>
    assertFails(updateDoc(doc(db("H2"), "hospitals/H2"), { isVerified: true })));

// ---------- requests ----------
it("verified hospital can create a valid request", () =>
    assertSucceeds(setDoc(doc(db("H1"), "requests/R2"), newRequest("R2", "H1"))));

it("a request without the optional system fields is still accepted", () => {
    const r = newRequest("R3", "H1");
    delete r.acceptedBy; delete r.acceptedDonors; delete r.rejectedDonors;
    delete r.completedBy; delete r.notifiedDonors; delete r.notificationRadius;
    return assertSucceeds(setDoc(doc(db("H1"), "requests/R3"), r));
});

it("an older build of the app (status pending, none of the Step 3 fields) is still accepted", () => {
    const r = newRequest("R4", "H1", { status: "pending" });
    for (const key of ["bloodComponent", "requiredWithinHours", "patientBloodGroupVerified", "unitsFulfilled", "responseCount"]) delete r[key];
    return assertSucceeds(setDoc(doc(db("H1"), "requests/R4"), r));
});

it("every blood component is accepted, an unknown one is not", async () => {
    for (const [i, component] of ["RED_CELLS", "WHOLE_BLOOD", "PLATELETS", "PLASMA"].entries()) {
        await assertSucceeds(setDoc(doc(db("H1"), `requests/C${i}`), newRequest(`C${i}`, "H1", { bloodComponent: component })));
    }
    await assertFails(setDoc(doc(db("H1"), "requests/C9"), newRequest("C9", "H1", { bloodComponent: "UNICORN_BLOOD" })));
});

it("'needed within' must be a whole number of hours from 1 to 168", async () => {
    await assertFails(setDoc(doc(db("H1"), "requests/T0"), newRequest("T0", "H1", { requiredWithinHours: 0 })));
    await assertFails(setDoc(doc(db("H1"), "requests/T1"), newRequest("T1", "H1", { requiredWithinHours: 200 })));
    await assertFails(setDoc(doc(db("H1"), "requests/T2"), newRequest("T2", "H1", { requiredWithinHours: 2.5 })));
    await assertSucceeds(setDoc(doc(db("H1"), "requests/T3"), newRequest("T3", "H1", { requiredWithinHours: 168 })));
});

it("a hospital cannot start a request with units already confirmed or responses already counted", async () => {
    await assertFails(setDoc(doc(db("H1"), "requests/U1"), newRequest("U1", "H1", { unitsFulfilled: 2 })));
    await assertFails(setDoc(doc(db("H1"), "requests/U2"), newRequest("U2", "H1", { responseCount: 5 })));
});

it("a hospital cannot change units or status afterwards", async () => {
    await assertFails(updateDoc(doc(db("H1"), "requests/R1"), { unitsFulfilled: 2 }));
    await assertFails(updateDoc(doc(db("H1"), "requests/R1"), { status: "fulfilled" }));
});

it("unverified hospital cannot create a request", () =>
    assertFails(setDoc(doc(db("H2"), "requests/R2"), newRequest("R2", "H2", { hospitalName: "Other" }))));

it("a donor cannot create a request", () =>
    assertFails(setDoc(doc(db("D1"), "requests/R2"), newRequest("R2", "D1"))));

it("a hospital cannot pre-fill notifiedDonors", () =>
    assertFails(setDoc(doc(db("H1"), "requests/R2"), newRequest("R2", "H1", { notifiedDonors: ["D1"] }))));

it("a hospital cannot spoof another hospital's name", () =>
    assertFails(setDoc(doc(db("H1"), "requests/R2"), newRequest("R2", "H1", { hospitalName: "Fake" }))));

it("nobody can update a request from the client", () =>
    assertFails(updateDoc(doc(db("H1"), "requests/R1"), { status: "completed" })));

// ---------- responses ----------
const response = (donorId, requestId = "R1", over = {}) => ({
    donorId, requestId, responseStatus: "accepted", attendanceStatus: "not_arrived",
    donationStatus: "not_started", respondedAt: serverTimestamp(), ...over
});

it("a donor can respond as themselves", () =>
    assertSucceeds(setDoc(doc(db("D1"), "requests/R1/responses/D1"), response("D1"))));

it("a donor cannot respond as someone else", () =>
    assertFails(setDoc(doc(db("D1"), "requests/R1/responses/D2"), response("D2"))));

it("a donor cannot respond to a request that was not sent to them", () =>
    assertFails(setDoc(doc(db("D2"), "requests/R1/responses/D2"), response("D2"))));

it("a donor cannot respond to a fulfilled request, but can while it is arranging", async () => {
    await assertFails(setDoc(doc(db("D1"), "requests/RF/responses/D1"), response("D1", "RF")));
    await assertSucceeds(setDoc(doc(db("D1"), "requests/RA/responses/D1"), response("D1", "RA")));
});

it("a donor can decline, but cannot invent another status", async () => {
    await assertSucceeds(setDoc(doc(db("D1"), "requests/R1/responses/D1"), response("D1", "R1", { responseStatus: "declined" })));
    await assertFails(setDoc(doc(db("D1"), "requests/RA/responses/D1"), response("D1", "RA", { responseStatus: "withdrawn" })));
    await assertFails(setDoc(doc(db("D1"), "requests/RA/responses/D1"), response("D1", "RA", { responseStatus: "maybe" })));
});

it("a donor cannot start with attendance or donation already recorded", async () => {
    await assertFails(setDoc(doc(db("D1"), "requests/R1/responses/D1"), response("D1", "R1", { attendanceStatus: "arrived" })));
    await assertFails(setDoc(doc(db("D1"), "requests/R1/responses/D1"), response("D1", "R1", { donationStatus: "collected" })));
});

it("the response time is the server's, not the phone's", () =>
    assertFails(setDoc(doc(db("D1"), "requests/R1/responses/D1"), response("D1", "R1", { respondedAt: new Date("2020-01-01") }))));

it("a donor can withdraw and change their mind before the hospital records the visit", async () => {
    const ref = doc(db("D1"), "requests/RU/responses/D1");
    await assertSucceeds(updateDoc(ref, { responseStatus: "withdrawn", respondedAt: serverTimestamp() }));
    await assertSucceeds(updateDoc(ref, { responseStatus: "accepted", respondedAt: serverTimestamp() }));
});

it("a donor cannot change anything once the hospital recorded their visit", () =>
    assertFails(updateDoc(doc(db("D2"), "requests/RU/responses/D2"), { responseStatus: "withdrawn", respondedAt: serverTimestamp() })));

it("a donor cannot touch attendance, donation or the hospital's fields", async () => {
    const ref = doc(db("D1"), "requests/RU/responses/D1");
    await assertFails(updateDoc(ref, { attendanceStatus: "arrived" }));
    await assertFails(updateDoc(ref, { donationStatus: "collected" }));
    await assertFails(updateDoc(ref, { hospitalNotified: true }));
    await assertFails(updateDoc(ref, { responseStatus: "withdrawn", respondedAt: serverTimestamp(), donationStatus: "collected" }));
});

it("a donor cannot change someone else's response, and a hospital cannot change a response from the app", async () => {
    await assertFails(updateDoc(doc(db("D1"), "requests/RU/responses/D2"), { responseStatus: "withdrawn", respondedAt: serverTimestamp() }));
    await assertFails(updateDoc(doc(db("H1"), "requests/RU/responses/D1"), { attendanceStatus: "arrived" }));
});

it("a response written before Step 4 (no responseStatus) can still be withdrawn", () =>
    assertSucceeds(updateDoc(doc(db("D1"), "requests/RL/responses/D1"), { responseStatus: "withdrawn", respondedAt: serverTimestamp() })));

it("a response cannot be deleted", () =>
    assertFails(deleteDoc(doc(db("D1"), "requests/RU/responses/D1"))));

it("a hospital cannot respond", () =>
    assertFails(setDoc(doc(db("H1"), "requests/R1/responses/H1"), response("H1"))));

it("a donor cannot list the responses; the owner hospital can", async () => {
    await assertFails(getDocs(collection(db("D1"), "requests/R1/responses")));
    await assertSucceeds(getDocs(collection(db("H1"), "requests/R1/responses")));
});

// ---------- directory / devices / stats ----------
it("only a verified hospital reads the donor directory", async () => {
    await assertSucceeds(getDoc(doc(db("H1"), "donorDirectory/D1")));
    await assertFails(getDoc(doc(db("H2"), "donorDirectory/D1")));
    await assertFails(getDoc(doc(db("D1"), "donorDirectory/D1")));
});

it("a user writes only their own token, and nobody can read tokens", async () => {
    const tokenDoc = { token: "x".repeat(150), platform: "android", updatedAt: serverTimestamp() };
    await assertSucceeds(setDoc(doc(db("D1"), "devices/D1"), tokenDoc));
    await assertFails(setDoc(doc(db("D1"), "devices/D2"), tokenDoc));
    await assertFails(getDoc(doc(db("D1"), "devices/D1")));
});

it("stats are readable but not writable", async () => {
    await assertSucceeds(getDoc(doc(db("D1"), "stats/global")));
    await assertFails(setDoc(doc(db("D1"), "stats/global"), { donorsCount: 999 }));
});