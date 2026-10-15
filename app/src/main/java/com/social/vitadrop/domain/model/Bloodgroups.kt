package com.social.vitadrop.domain.model


/**
 * The 8 official blood groups. The server finds donors by comparing this text EXACTLY
 * (functions/utils/bloodCompatibility.js), so every screen must save one of these values.
 */
object BloodGroups {

    val ALL = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    /** "o+" or " O+ " becomes "O+". Anything that is not a blood group becomes null. */
    fun normalize(input: String): String? {
        val cleaned = input.trim().uppercase().replace(" ", "")
        return cleaned.takeIf { it in ALL }
    }
}