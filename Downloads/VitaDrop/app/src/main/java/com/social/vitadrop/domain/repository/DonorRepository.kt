package com.social.vitadrop.domain.repository


import com.social.vitadrop.domain.model.DonorModel

interface DonorRepository {
    suspend fun getAllDonors(): List<DonorModel>
}