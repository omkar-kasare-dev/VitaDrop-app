package com.social.vitadrop.domain.model


enum class Urgency(val key: String) {
    LOW("low"), MEDIUM("medium"), HIGH("high");

    companion object {
        fun fromKey(key: String): Urgency =
            values().firstOrNull { it.key == key.trim().lowercase() } ?: MEDIUM
    }
}