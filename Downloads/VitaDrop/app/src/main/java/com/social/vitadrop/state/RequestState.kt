package com.social.vitadrop.state

import com.social.vitadrop.domain.model.RequestModel


data class RequestState(

    // 👤 Patient Info
    val patientName: String = "",
    val bloodGroup: String = "",

    // 🏥 Hospital Info
    val hospitalName: String = "",
    val city: String = "",

    // 📞 Contact
    val contactNumber: String = "",

    // 🩸 Request Details
    val unitsRequired: String = "",
    val requestType: String = "blood",

    // 🚨 Emergency
    val isEmergency: Boolean = false,

    // ⚙️ UI States
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,

    // ❌ Optional future use (recommended)
    val errorMessage: String? = null,
    //

)