package com.social.vitadrop.domain.model

enum class UserRole(val key: String) {
    DONOR("donor"),
    HOSPITAL("hospital");

    companion object {
        fun fromKey(key: String): UserRole? =
            values().firstOrNull { it.key == key.trim().lowercase() }
    }
}