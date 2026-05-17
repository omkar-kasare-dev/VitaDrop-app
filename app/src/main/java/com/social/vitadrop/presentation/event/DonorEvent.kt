package com.social.vitadrop.presentation.event


/*
sealed class DonorEvent {
    object LoadDonors : DonorEvent()
}

 */

// Modified Donor Event:


sealed class DonorEvent {

    // LOAD ALL DONORS
    object LoadDonors : DonorEvent()

    // REFRESH DONOR LIST
    object RefreshDonors : DonorEvent()

    // SEARCH DONORS
    data class SearchDonors(
        val query: String
    ) : DonorEvent()

    // FILTER BY BLOOD GROUP
    data class FilterByBloodGroup(
        val bloodGroup: String
    ) : DonorEvent()

    // FILTER BY CITY
    data class FilterByCity(
        val city: String
    ) : DonorEvent()

    // CLEAR ALL FILTERS
    object ClearFilters : DonorEvent()
}