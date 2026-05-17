package com.social.vitadrop.state

import com.social.vitadrop.domain.model.RequestModel

/*
data class RequestState(

    // Patient Info
    val patientName: String = "",
    val bloodGroup: String = "",

    // Hospital Info
    val hospitalName: String = "",
    val city: String = "",

    //  Contact
    val contactNumber: String = "",

    // Request Details
    val unitsRequired: String = "",
    val requestType: String = "blood",

    //Emergency
    val isEmergency: Boolean = false,

    //  UI States
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,

    // Optional future use
    val errorMessage: String? = null,


)

 */

// Modified request State.




data class RequestState(

    // =========================
    // REQUEST INFO
    // =========================

    val requestId: String = "",

    val requestedBy: String = "",

    val hospitalId: String = "",


    // =========================
    // PATIENT INFO
    // =========================

    val patientName: String = "",

    val bloodGroup: String = "",

    val unitsRequired: String = "",


    // =========================
    // CONTACT INFO
    // =========================

    val contactPerson: String = "",

    val contactNumber: String = "",


    // =========================
    // HOSPITAL INFO
    // =========================

    val hospitalName: String = "",

    val city: String = "",


    // =========================
    // LOCATION
    // =========================

    val latitude: String = "",

    val longitude: String = "",


    // =========================
    // REQUEST DETAILS
    // =========================

    val requestType: String = "blood",

    val urgency: String = "medium",

    val status: String = "pending",

    val description: String = "",


    // =========================
    // DONOR TRACKING
    // =========================

    val acceptedDonors: List<String> = emptyList(),

    val rejectedDonors: List<String> = emptyList(),

    val completedBy: List<String> = emptyList(),


    // =========================
    // EMERGENCY
    // =========================

    val isEmergency: Boolean = false,


    // =========================
    // UI STATES
    // =========================

    val isLoading: Boolean = false,

    val isSuccess: Boolean = false,

    val errorMessage: String? = null,


    // =========================
    // REQUEST LIST
    // =========================

    val requests: List<RequestModel> = emptyList()
)