package com.social.vitadrop.domain.model



data class User(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val address: String = ""
)