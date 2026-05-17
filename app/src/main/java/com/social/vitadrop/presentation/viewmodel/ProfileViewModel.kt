package com.social.vitadrop.presentation.viewmodel
/*
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

                //Load role-based extra data
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

 */

// Updated ViewModel


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

        val currentUser = auth.currentUser ?: return

        state = state.copy(
            isLoading = true
        )

        // FIRST CHECK DONOR COLLECTION
        db.collection("donors")
            .document(currentUser.uid)
            .get()

            .addOnSuccessListener { donorDoc ->

                if (donorDoc.exists()) {

                    val location =
                        donorDoc.get("location") as? Map<*, *>

                    state = state.copy(

                        uid = currentUser.uid,
                        role = "donor",

                        // Basic
                        fullName = donorDoc.getString("fullName")
                            ?: "",

                        email = donorDoc.getString("email")
                            ?: "",

                        phone = donorDoc.getString("phone")
                            ?: "",

                        gender = donorDoc.getString("gender")
                            ?: "",

                        age = donorDoc.getLong("age")
                            ?.toString() ?: "",

                        // Blood
                        bloodGroup = donorDoc.getString("bloodGroup")
                            ?: "",

                        // Address
                        city = donorDoc.getString("city")
                            ?: "",

                        state = donorDoc.getString("state")
                            ?: "",

                        address = donorDoc.getString("address")
                            ?: "",

                        // Location
                        latitude = location?.get("latitude")
                            ?.toString() ?: "",

                        longitude = location?.get("longitude")
                            ?.toString() ?: "",

                        // Profile
                        profileImage = donorDoc.getString("profileImage")
                            ?: "",

                        weight = donorDoc.getDouble("weight")
                            ?.toString() ?: "",

                        // Status
                        isAvailable = donorDoc.getBoolean("isAvailable")
                            ?: false,

                        isVerified = donorDoc.getBoolean("isVerified")
                            ?: false,

                        isBlocked = donorDoc.getBoolean("isBlocked")
                            ?: false,

                        isLoading = false
                    )

                } else {

                    // CHECK HOSPITAL COLLECTION
                    db.collection("hospitals")
                        .document(currentUser.uid)
                        .get()

                        .addOnSuccessListener { hospitalDoc ->

                            if (hospitalDoc.exists()) {

                                val location =
                                    hospitalDoc.get("location") as? Map<*, *>

                                state = state.copy(

                                    uid = currentUser.uid,
                                    role = "hospital",

                                    // Basic
                                    fullName = hospitalDoc.getString("hospitalName")
                                        ?: "",

                                    email = hospitalDoc.getString("email")
                                        ?: "",

                                    phone = hospitalDoc.getString("phone")
                                        ?: "",

                                    // Hospital
                                    licenseNumber = hospitalDoc.getString("licenseNumber")
                                        ?: "",

                                    // Address
                                    city = hospitalDoc.getString("city")
                                        ?: "",

                                    state = hospitalDoc.getString("state")
                                        ?: "",

                                    address = hospitalDoc.getString("address")
                                        ?: "",

                                    // Location
                                    latitude = location?.get("latitude")
                                        ?.toString() ?: "",

                                    longitude = location?.get("longitude")
                                        ?.toString() ?: "",

                                    // Profile
                                    profileImage = hospitalDoc.getString("profileImage")
                                        ?: "",

                                    // Status
                                    isVerified = hospitalDoc.getBoolean("isVerified")
                                        ?: false,

                                    isBlocked = hospitalDoc.getBoolean("isBlocked")
                                        ?: false,

                                    isLoading = false
                                )

                            } else {

                                // ADMIN COLLECTION
                                db.collection("admin")
                                    .document(currentUser.uid)
                                    .get()

                                    .addOnSuccessListener { adminDoc ->

                                        if (adminDoc.exists()) {

                                            state = state.copy(

                                                uid = currentUser.uid,
                                                role = "admin",

                                                fullName = adminDoc.getString("fullName")
                                                    ?: "",

                                                email = adminDoc.getString("email")
                                                    ?: "",

                                                phone = adminDoc.getString("phone")
                                                    ?: "",

                                                isBlocked = adminDoc.getBoolean("isBlocked")
                                                    ?: false,

                                                isLoading = false
                                            )

                                        } else {

                                            state = state.copy(
                                                isLoading = false,
                                                message = "User data not found"
                                            )
                                        }
                                    }

                                    .addOnFailureListener {

                                        state = state.copy(
                                            isLoading = false,
                                            message = it.message ?: "Failed to load admin data"
                                        )
                                    }
                            }
                        }

                        .addOnFailureListener {

                            state = state.copy(
                                isLoading = false,
                                message = it.message ?: "Failed to load hospital data"
                            )
                        }
                }
            }

            .addOnFailureListener {

                state = state.copy(
                    isLoading = false,
                    message = it.message ?: "Failed to load donor data"
                )
            }
    }

    fun logout(
        sessionManager: SessionManager,
        onLogout: () -> Unit
    ) {

        auth.signOut()

        sessionManager.clearSession()

        onLogout()
    }
}