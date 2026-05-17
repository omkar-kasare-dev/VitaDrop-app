package com.social.vitadrop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.social.vitadrop.data.repository.NotificationRepositoryImpl
import com.social.vitadrop.domain.usecase.GetFCMTokenUseCase
import com.social.vitadrop.domain.usecase.SaveFCMTokenUseCase
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    /**
     * =====================================================
     * MANUAL DEPENDENCY CREATION
     * =====================================================
     */
    private val repository =
        NotificationRepositoryImpl()

    private val getFCMTokenUseCase =
        GetFCMTokenUseCase(repository)

    private val saveFCMTokenUseCase =
        SaveFCMTokenUseCase(repository)

    private val auth =
        FirebaseAuth.getInstance()

    /**
     * =====================================================
     * GENERATE + SAVE DONOR TOKEN
     * =====================================================
     */
    fun generateAndSaveDonorToken() {

        viewModelScope.launch {

            try {

                val token =
                    getFCMTokenUseCase()

                val donorId =
                    auth.currentUser?.uid
                        ?: return@launch

                saveFCMTokenUseCase
                    .saveDonorToken(
                        donorId,
                        token
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    /**
     * =====================================================
     * GENERATE + SAVE HOSPITAL TOKEN
     * =====================================================
     */
    fun generateAndSaveHospitalToken() {

        viewModelScope.launch {

            try {

                val token =
                    getFCMTokenUseCase()

                val hospitalId =
                    auth.currentUser?.uid
                        ?: return@launch

                saveFCMTokenUseCase
                    .saveHospitalToken(
                        hospitalId,
                        token
                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}