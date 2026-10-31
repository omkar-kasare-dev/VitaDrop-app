package com.social.vitadrop.domain.model

/*
data class DonorResponse(

    val donorId: String = "",

    val requestId: String = "",

    val status: String = "accepted",

    val responseTime: Long =
        System.currentTimeMillis()
)

 */



/**
 * What the app knows about one donor's response to one request (read side).
 * Writing a response is done by ResponseRepositoryImpl, which uses a server timestamp.
 */
data class DonorResponse(

    val donorId: String = "",

    val requestId: String = "",

    val responseStatus: ResponseStatus = ResponseStatus.ACCEPTED,

    val attendanceStatus: AttendanceStatus = AttendanceStatus.NOT_ARRIVED,

    val donationStatus: DonationStatus = DonationStatus.NOT_STARTED
) {
    /** The donor may still change their mind until the hospital has recorded the visit. */
    val canChange: Boolean
        get() = attendanceStatus == AttendanceStatus.NOT_ARRIVED &&
                donationStatus == DonationStatus.NOT_STARTED
}