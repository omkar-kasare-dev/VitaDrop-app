package com.social.vitadrop.domain.model
/*
import com.google.firebase.Timestamp

//Modified request Model.
data class RequestModel(

    // REQUEST INFO
    val requestId: String = "",

    val requestedBy: String = "", // hospital / admin

    val hospitalId: String = "",

    // PATIENT INFO
    val patientName: String = "",

    // BLOOD INFO
    val bloodGroup: String = "",

    //val unitsRequired: String = "",
    val unitsRequired: Long = 0,

    // CONTACT INFO
    val contactPerson: String = "",

    val contactPhone: String = "",

    // HOSPITAL INFO
    val hospitalName: String = "",

    val city: String = "",

    // LOCATION
    val location: Map<String, Double> = mapOf(

        "latitude" to 0.0,
        "longitude" to 0.0
    ),

    // REQUEST DETAILS
    val urgency: String = "medium",

    val status: String = "pending",

    val description: String = "",

    // DONOR TRACKING
    val acceptedDonors: List<String> = emptyList(),

    // number of donors who tapped "I can donate" (written by a Cloud Function)
    val responseCount: Long = 0,

    // NOTIFICATION TRACKING
    val notificationRadius: Int = 5,

    val acceptedBy: String = "",

    //val notificationStartedAt: Long? = null,
    val notificationStartedAt: Timestamp? = null,


    val notifiedDonors: List<String> = emptyList(),

    val rejectedDonors: List<String> = emptyList(),

    val completedBy: List<String> = emptyList(),

    // EMERGENCY
    val emergency: Boolean = false,

    // TIMESTAMP
    // val createdAt: Long? = null
    val createdAt: Timestamp? = null
)

 */


import com.google.firebase.Timestamp

//Modified request Model.
data class RequestModel(

    // REQUEST INFO
    val requestId: String = "",

    val requestedBy: String = "", // hospital / admin

    val hospitalId: String = "",

    // PATIENT INFO
    val patientName: String = "",

    // BLOOD INFO
    val bloodGroup: String = "",

    // RED_CELLS, WHOLE_BLOOD, PLATELETS or PLASMA (see BloodComponent)
    val bloodComponent: String = "RED_CELLS",

    // the hospital says its lab verified the patient's group (VitaDrop never decides compatibility)
    val patientBloodGroupVerified: Boolean = false,

    //val unitsRequired: String = "",
    val unitsRequired: Long = 0,

    // units the HOSPITAL confirmed as arranged. Written only by a Cloud Function.
    val unitsFulfilled: Long = 0,

    // deadline, set by the server from the "needed within" choice
    val requiredBy: Timestamp? = null,

    // CONTACT INFO
    val contactPerson: String = "",

    val contactPhone: String = "",

    // HOSPITAL INFO
    val hospitalName: String = "",

    val city: String = "",

    // LOCATION
    val location: Map<String, Double> = mapOf(

        "latitude" to 0.0,
        "longitude" to 0.0
    ),

    // REQUEST DETAILS
    val urgency: String = "medium",

    // always one of the RequestStatus keys (the mapper renames "pending" and "accepted")
    val status: String = "searching",

    val description: String = "",

    // DONOR TRACKING
    val acceptedDonors: List<String> = emptyList(),

    // number of donors who tapped "I can donate" (written by a Cloud Function)
    val responseCount: Long = 0,

    // NOTIFICATION TRACKING
    val notificationRadius: Int = 5,

    val acceptedBy: String = "",

    //val notificationStartedAt: Long? = null,
    val notificationStartedAt: Timestamp? = null,


    val notifiedDonors: List<String> = emptyList(),

    val rejectedDonors: List<String> = emptyList(),

    val completedBy: List<String> = emptyList(),

    // EMERGENCY
    val emergency: Boolean = false,

    // TIMESTAMP
    // val createdAt: Long? = null
    val createdAt: Timestamp? = null
)