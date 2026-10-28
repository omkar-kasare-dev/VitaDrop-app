// KEY   = blood group of the PATIENT (the request)
// VALUE = donor blood groups that can give red blood cells to that patient
const COMPATIBILITY = {

    "O-": ["O-"],

    "O+": ["O+", "O-"],

    "A-": ["A-", "O-"],

    "A+": ["A+", "A-", "O+", "O-"],

    "B-": ["B-", "O-"],

    "B+": ["B+", "B-", "O+", "O-"],

    "AB-": [
        "AB-",
        "A-",
        "B-",
        "O-"
    ],

    "AB+": [
        "AB+",
        "AB-",
        "A+",
        "A-",
        "B+",
        "B-",
        "O+",
        "O-"
    ]
};

/**
 * Turns what a person typed into one of the 8 official values, or null if it is not a blood group.
 *   "o+"  "O +"  "O positive"  "O+ve"  "o-ve"  "AB NEGATIVE"   ->   "O+" "O+" "O+" "O+" "O-" "AB-"
 *
 * Firestore queries compare text EXACTLY. Before this, a donor who typed "o+" was never found
 * for an "O+" request, so no notification was ever sent to them.
 */
function normalizeBloodGroup(value) {

    if (value === null || value === undefined) {
        return null;
    }

    const cleaned = String(value)
        .trim()
        .toUpperCase()
        .replace(/\s+/g, "")
        .replace(/[−–]/g, "-")
        .replace(/POSITIVE|POS|\+VE/g, "+")
        .replace(/NEGATIVE|NEG|-VE/g, "-");

    return COMPATIBILITY[cleaned] ? cleaned : null;
}

// ---------------- STEP 3: the blood component ----------------

const COMPONENT_LABELS = {
    RED_CELLS: "red blood cells",
    WHOLE_BLOOD: "whole blood",
    PLATELETS: "platelets",
    PLASMA: "plasma"
};

/** Anything unknown (and every request created before Step 3) counts as RED_CELLS, as before. */
function normalizeComponent(value) {

    const key = String(value || "")
        .trim()
        .toUpperCase()
        .replace(/[\s-]+/g, "_");

    return COMPONENT_LABELS[key] ? key : "RED_CELLS";
}

/**
 * Which donor blood groups are worth ALERTING for this request.
 *
 *  - RED_CELLS: the red-cell table above.
 *  - WHOLE_BLOOD, PLATELETS, PLASMA: the SAME group only. The red-cell table is wrong for
 *    plasma (it works the other way round) and for platelets, so the safe choice for now is
 *    an exact match.
 *
 * This only decides who gets a notification. It is never a compatibility decision:
 * the blood bank tests and crossmatches every unit.
 */
function candidateGroups(patientBloodGroup, component) {

    const group = normalizeBloodGroup(patientBloodGroup);

    if (!group) {
        return [];
    }

    return normalizeComponent(component) === "RED_CELLS"
        ? COMPATIBILITY[group]
        : [group];
}

module.exports = {
    COMPATIBILITY,
    COMPONENT_LABELS,
    normalizeBloodGroup,
    normalizeComponent,
    candidateGroups
};