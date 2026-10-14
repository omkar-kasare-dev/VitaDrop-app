package com.social.vitadrop.domain.repository

import com.social.vitadrop.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>
}