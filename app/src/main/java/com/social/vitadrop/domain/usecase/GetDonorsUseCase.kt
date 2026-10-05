package com.social.vitadrop.domain.usecase

import com.social.vitadrop.domain.model.DonorModel
import com.social.vitadrop.domain.repository.DonorRepository

class GetDonorsUseCase(
    private val repository: DonorRepository
) {

    /**
     * Business rules (moved here from the old ViewModel):
     *  - blocked donors are never listed
     *  - available donors come first, then alphabetical
     */
    suspend operator fun invoke(): Result<List<DonorModel>> =
        repository.getAllDonors().map { donors ->
            donors
                .filter { !it.isBlocked }
                .sortedWith(
                    compareByDescending<DonorModel> { it.isAvailable }
                        .thenBy { it.fullName.lowercase() }
                )
        }
}