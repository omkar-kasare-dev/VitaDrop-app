package com.social.vitadrop.presentation.event



sealed class DonorDashboardEvent {

    object LoadDashboard : DonorDashboardEvent()

    object OnAddDonorClick : DonorDashboardEvent()
    object OnProfileClick : DonorDashboardEvent()

    object OnRequestBloodClick : DonorDashboardEvent()
    object OnAddHospitalClick : DonorDashboardEvent()
    object OnDonorListClick : DonorDashboardEvent()
    object OnBloodCampClick : DonorDashboardEvent()
    object LoadRequests : DonorDashboardEvent()
}