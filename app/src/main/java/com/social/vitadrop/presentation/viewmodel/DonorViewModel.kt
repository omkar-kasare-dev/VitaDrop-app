package com.social.vitadrop.presentation.viewmodel


/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.repository.DonorRepository
import com.social.vitadrop.presentation.event.DonorEvent
import com.social.vitadrop.state.DonorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DonorViewModel(
    private val repository: DonorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DonorState())
    val state: StateFlow<DonorState> = _state

    fun onEvent(event: DonorEvent) {
        when (event) {
            is DonorEvent.LoadDonors -> loadDonors()
        }
    }

    private fun loadDonors() {

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {

                val donors = repository.getAllDonors()

                _state.value = _state.value.copy(
                    donors = donors,
                    isLoading = false
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}*/

// Modified Donor ViewModel:


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.DonorModel
import com.social.vitadrop.domain.repository.DonorRepository
import com.social.vitadrop.presentation.event.DonorEvent
import com.social.vitadrop.state.DonorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DonorViewModel(
    private val repository: DonorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DonorState())

    val state: StateFlow<DonorState> = _state

    fun onEvent(event: DonorEvent) {

        when (event) {

            is DonorEvent.LoadDonors -> {
                loadDonors()
            }
            else->{
                println("error")
            }
        }
    }

    private fun loadDonors() {

        viewModelScope.launch {

            _state.value = _state.value.copy(

                isLoading = true,
                error = null
            )

            try {

                // LOAD DONORS FROM FIRESTORE
                val donors = repository.getAllDonors()

                // FILTER BLOCKED USERS
                val activeDonors = donors.filter {

                    !it.isBlocked
                }

                // SORT AVAILABLE DONORS FIRST
                val sortedDonors = activeDonors.sortedByDescending {

                    it.isAvailable
                }

                _state.value = _state.value.copy(

                    donors = sortedDonors,

                    isLoading = false,

                    error = null
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(

                    isLoading = false,

                    error = e.message
                        ?: "Failed to load donors"
                )
            }
        }
    }

    // OPTIONAL REFRESH FUNCTION
    fun refreshDonors() {

        loadDonors()
    }

    // OPTIONAL SEARCH FILTER
    fun searchDonors(
        query: String
    ) {

        viewModelScope.launch {

            try {

                val allDonors = repository.getAllDonors()

                val filteredDonors = allDonors.filter {

                    it.fullName.contains(
                        query,
                        ignoreCase = true
                    ) ||

                            it.bloodGroup.contains(
                                query,
                                ignoreCase = true
                            ) ||

                            it.city.contains(
                                query,
                                ignoreCase = true
                            ) ||

                            it.state.contains(
                                query,
                                ignoreCase = true
                            )
                }

                _state.value = _state.value.copy(
                    donors = filteredDonors
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    error = e.message
                )
            }
        }
    }

    // OPTIONAL BLOOD GROUP FILTER
    fun filterByBloodGroup(
        bloodGroup: String
    ) {

        viewModelScope.launch {

            try {

                val allDonors = repository.getAllDonors()

                val filteredDonors = allDonors.filter {

                    it.bloodGroup == bloodGroup
                }

                _state.value = _state.value.copy(
                    donors = filteredDonors
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    error = e.message
                )
            }
        }
    }

    // OPTIONAL CITY FILTER
    fun filterByCity(
        city: String
    ) {

        viewModelScope.launch {

            try {

                val allDonors = repository.getAllDonors()

                val filteredDonors = allDonors.filter {

                    it.city.equals(
                        city,
                        ignoreCase = true
                    )
                }

                _state.value = _state.value.copy(
                    donors = filteredDonors
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    error = e.message
                )
            }
        }
    }

    // CLEAR FILTERS
    fun clearFilters() {

        loadDonors()
    }
}