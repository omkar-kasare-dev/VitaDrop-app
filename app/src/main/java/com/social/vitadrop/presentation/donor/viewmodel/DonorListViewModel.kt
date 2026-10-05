package com.social.vitadrop.presentation.donor.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.usecase.GetDonorsUseCase
import com.social.vitadrop.presentation.donor.DonorListIntent
import com.social.vitadrop.presentation.donor.DonorListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DonorListViewModel(
    private val getDonors: GetDonorsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DonorListState())
    val state: StateFlow<DonorListState> = _state.asStateFlow()

    // Load once when the ViewModel is created (survives rotation, no reload loop)
    init {
        load()
    }

    fun onIntent(intent: DonorListIntent) {
        when (intent) {
            DonorListIntent.Refresh ->
                load()

            is DonorListIntent.Search ->
                _state.update { it.copy(query = intent.query) }

            is DonorListIntent.FilterBloodGroup ->
                _state.update { it.copy(bloodGroup = intent.bloodGroup) }

            is DonorListIntent.FilterGender ->
                _state.update { it.copy(gender = intent.gender) }

            is DonorListIntent.FilterCity ->
                _state.update { it.copy(city = intent.city) }

            is DonorListIntent.AvailableOnlyChanged ->
                _state.update { it.copy(availableOnly = intent.value) }

            DonorListIntent.ClearFilters ->
                _state.update {
                    it.copy(
                        query = "",
                        bloodGroup = null,
                        gender = null,
                        city = null,
                        availableOnly = false
                    )
                }
        }
    }

    private fun load() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getDonors()
                .onSuccess { donors ->
                    _state.update { it.copy(allDonors = donors, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to load donors")
                    }
                }
        }
    }
}