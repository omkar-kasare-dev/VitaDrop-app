package com.social.vitadrop.domain.model

data class DonorModel(
    val id: String = "",
    val name: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val phone: String = "",
    val isAvailable: Boolean = true
)