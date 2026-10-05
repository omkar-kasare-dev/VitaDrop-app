package com.social.vitadrop.presentation.donor

import com.social.vitadrop.domain.model.DonorModel

data class DonorListState(
    val allDonors: List<DonorModel> = emptyList(),
    val query: String = "",
    val bloodGroup: String? = null,
    val city: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    // Derived, never stored: filters can't get out of sync with the list
    val visibleDonors: List<DonorModel>
        get() = allDonors.filter { d ->
            (bloodGroup == null || d.bloodGroup == bloodGroup) &&
                    (city == null || d.city.equals(city, ignoreCase = true)) &&
                    (query.isBlank() || d.fullName.contains(query, ignoreCase = true))
        }
}

sealed interface DonorListIntent {
    data object Load : DonorListIntent
    data object Refresh : DonorListIntent
    data class Search(val query: String) : DonorListIntent
    data class FilterBloodGroup(val bloodGroup: String?) : DonorListIntent
    data class FilterCity(val city: String?) : DonorListIntent
    data object ClearFilters : DonorListIntent
}