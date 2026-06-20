package com.social.vitadrop.domain.model


data class DonorResponse(

    val donorId: String = "",

    val requestId: String = "",

    val status: String = "accepted",

    val responseTime: Long =
        System.currentTimeMillis()
)