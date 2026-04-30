package com.social.vitadrop.domain.model



data class RequestModel(
    val patientName: String = "",
    val bloodGroup: String = "",
    val unitsRequired: String = "",
    val hospitalName: String = "",
    val city: String = "",
    val contactNumber: String = "",
    val requestType: String = "blood",
    val isEmergency: Boolean = false,
    val status: String = "pending",
    val createdBy: String = "",
    val createdAt: Long = 0
)
