package com.social.vitadrop.data.remote

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.data.mapper.collectionName
import com.social.vitadrop.data.mapper.toFirestoreMap
import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.model.UserRole
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

class FirebaseAuthService {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String, role: UserRole): Result<String> {
        return try {
            // STEP 1: check email + password
            val uid = auth
                .signInWithEmailAndPassword(email.trim(), password)
                .await()
                .user?.uid
                ?: return Result.failure(Exception("User ID is null"))

            // STEP 2: check the account really belongs to the selected role
            val profile = withTimeoutOrNull(15_000) {
                db.collection(role.collectionName).document(uid).get().await()
            }

            when {
                profile == null -> {
                    auth.signOut()
                    Result.failure<String>(
                        IOException("Could not reach the server. Check your internet connection and try again.")
                    )
                }
                !profile.exists() -> {
                    auth.signOut()
                    Result.failure<String>(
                        Exception("No ${role.key} account found for this email. Please select the correct role.")
                    )
                }
                profile.getBoolean("isBlocked") == true -> {
                    auth.signOut()
                    Result.failure<String>(Exception("This account has been blocked. Please contact support."))
                }
                else -> Result.success(uid)
            }

        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Invalid email or password."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Invalid email or password."))
        } catch (e: FirebaseNetworkException) {
            Result.failure(Exception("Network error. Check your internet connection."))
        } catch (e: Exception) {
            Log.e("VITA_AUTH", "Login failed", e)
            Result.failure(e)
        }
    }

    suspend fun register(user: User, password: String): Result<String> {
        return try {
            if (user.email.isBlank()) {
                return Result.failure(Exception("Email cannot be empty"))
            }
            if (password.length < 6) {
                return Result.failure(Exception("Password must contain at least 6 characters"))
            }

            // STEP 1: create the Auth user
            val firebaseUser = auth
                .createUserWithEmailAndPassword(user.email.trim(), password)
                .await()
                .user
                ?: return Result.failure(Exception("Firebase user creation failed"))

            val uid = firebaseUser.uid
            Log.d("VITA_REGISTER", "Auth user created: $uid")

            // STEP 2: create the Firestore profile (timeout + rollback)
            try {
                val saved = withTimeoutOrNull(15_000) {
                    db.collection(user.role.collectionName)
                        .document(uid)
                        .set(user.toFirestoreMap(uid))
                        .await()
                    true
                }
                if (saved == null) {
                    throw IOException("Could not reach the server. Check your internet connection and try again.")
                }
            } catch (e: Exception) {
                Log.e("VITA_REGISTER", "Profile write failed, rolling back Auth user", e)
                runCatching { firebaseUser.delete().await() }
                throw e
            }

            Log.d("VITA_REGISTER", "SUCCESS: ${user.role.collectionName}/$uid created")
            Result.success(uid)

        } catch (e: Exception) {
            Log.e("VITA_REGISTER", "Registration failed", e)
            Result.failure(e)
        }
    }
}