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

module.exports = {
    COMPATIBILITY,
    normalizeBloodGroup
};