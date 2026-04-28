package com.social.vitadrop.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.state.ProfileState
import com.social.vitadrop.utils.SessionManager

class ProfileViewModel(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    fun loadUser() {

        val user = auth.currentUser ?: return

        state = state.copy(isLoading = true)

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->

                val role = doc.getString("role") ?: ""

                state = state.copy(
                    role = role,
                    email = doc.getString("email") ?: "",
                    name = doc.getString("name") ?: "",
                    phone = doc.getString("phone") ?: "",
                    city = doc.getString("city") ?: "",
                    isLoading = false
                )

                // 👉 Load role-based extra data
                loadRoleData(user.uid, role)
            }
    }

    private fun loadRoleData(uid: String, role: String) {

        val collection = when (role) {
            "donor" -> "donors"
            "hospital" -> "hospitals"
            "admin" -> "admins"
            else -> return
        }

        db.collection(collection)
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->

                state = state.copy(
                    bloodGroup = doc.getString("bloodGroup") ?: "",
                    city = doc.getString("city") ?: state.city
                )
            }
    }

    fun logout(sessionManager: SessionManager, onLogout: () -> Unit) {

        auth.signOut()
        sessionManager.clearSession()
        onLogout()
    }
}