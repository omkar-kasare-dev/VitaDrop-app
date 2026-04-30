package com.social.vitadrop.state


import com.social.vitadrop.domain.model.DonorModel

data class DonorState(
    val donors: List<DonorModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)