package com.social.vitadrop.domain.model

/**
 * What the patient needs. The hospital's clinician decides it: VitaDrop only passes it on, so
 * donors are told what is wanted and the right donors are alerted.
 *
 * The keys must match the Cloud Functions and the Firestore rules.
 * A request without a component (created before Step 3) is RED_CELLS, as before.
 */
enum class BloodComponent(val key: String, val label: String) {
    RED_CELLS("RED_CELLS", "Red cells"),
    WHOLE_BLOOD("WHOLE_BLOOD", "Whole blood"),
    PLATELETS("PLATELETS", "Platelets"),
    PLASMA("PLASMA", "Plasma");

    companion object {
        fun fromKey(key: String?): BloodComponent =
            entries.firstOrNull { it.key == key?.trim()?.uppercase()?.replace(' ', '_') } ?: RED_CELLS
    }
}