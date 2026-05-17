package com.social.vitadrop.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.social.vitadrop.data.remote.FirebaseAuthService
import com.social.vitadrop.data.repository.AuthRepositoryImpl
import com.social.vitadrop.domain.usecase.LoginUseCase
import com.social.vitadrop.utils.SessionManager

class AuthViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val service = FirebaseAuthService()
        val repository = AuthRepositoryImpl(service)
        val useCase = LoginUseCase(repository)
        val sessionManager = SessionManager(context)

        return AuthViewModel(useCase, sessionManager) as T
    }
}