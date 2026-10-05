package com.social.vitadrop.presentation.donor

import com.social.vitadrop.domain.model.DonorModel

// STATE: everything the donor list screen needs
data class DonorListState(
    val allDonors: List<DonorModel> = emptyList(),

    // filters (null = "All")
    val query: String = "",
    val bloodGroup: String? = null,
    val gender: String? = null,
    val city: String? = null,
    val availableOnly: Boolean = false,

    val isLoading: Boolean = false,
    val error: String? = null
) {
    // DERIVED, never set by hand: recalculated every time the state changes,
    // so the visible list can never get out of sync with the filters.
    val visibleDonors: List<DonorModel> = allDonors.filter { d ->
        val q = query.trim()

        (q.isEmpty() ||
                d.fullName.contains(q, ignoreCase = true) ||
                d.city.contains(q, ignoreCase = true) ||
                d.bloodGroup.contains(q, ignoreCase = true)) &&
                (bloodGroup == null || d.bloodGroup.equals(bloodGroup, ignoreCase = true)) &&
                (gender == null || d.gender.equals(gender, ignoreCase = true)) &&
                (city == null || d.city.trim().equals(city, ignoreCase = true)) &&
                (!availableOnly || d.isAvailable)
    }

    // City options for the filter dropdown
    val cities: List<String> = allDonors
        .map { it.city.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()
}

// INTENT: everything the user can do on this screen
sealed interface DonorListIntent {
    data object Refresh : DonorListIntent                       // also used by the Retry button
    data class Search(val query: String) : DonorListIntent
    data class FilterBloodGroup(val bloodGroup: String?) : DonorListIntent
    data class FilterGender(val gender: String?) : DonorListIntent
    data class FilterCity(val city: String?) : DonorListIntent
    data class AvailableOnlyChanged(val value: Boolean) : DonorListIntent
    data object ClearFilters : DonorListIntent
}