package com.social.vitadrop.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.RequestModel
import kotlinx.coroutines.tasks.await

class DashboardRepositoryImpl : DashboardRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun getDonorCount(): Int {
        return try {
            val result = db.collection("donors").get().await()
            Log.d("FIREBASE_DEBUG", "Donors: ${result.size()}")
            result.size()
        } catch (e: Exception) {
            Log.e("FIREBASE_ERROR", e.message ?: "")
            0
        }
    }

    override suspend fun getHospitalCount(): Int {
        return try {
            val result = db.collection("hospitals").get().await()
            Log.d("FIREBASE_DEBUG", "Hospitals: ${result.size()}")
            result.size()
        } catch (e: Exception) {
            Log.e("FIREBASE_ERROR", e.message ?: "")
            0
        }
    }

    override suspend fun getRequestCount(): Int {
        return try {
            val result = db.collection("requests").get().await()
            Log.d("FIREBASE_DEBUG", "Requests: ${result.size()}")
            result.size()
        } catch (e: Exception) {
            Log.e("FIREBASE_ERROR", e.message ?: "")
            0
        }
    }

    override suspend fun getEmergencyRequests(): List<RequestModel> {
        return try {

            val result = db.collection("requests")
                .whereEqualTo("isEmergency", true)
                .get()
                .await()

            result.documents.forEach {
                Log.d("DEBUG_DOC", it.data.toString())
            }

            Log.d("FIREBASE_DEBUG", "Emergency Requests: ${result.size()}")

            result.documents
                .filter {
                it.getBoolean("isEmergency") == true
            }
                .map { doc ->

                RequestModel(
                    patientName = doc.getString("patientName") ?: "",
                    bloodGroup = doc.getString("bloodGroup") ?: "",
                    unitsRequired = doc.getString("unitsRequired")?: "",
                    hospitalName = doc.getString("hospitalName") ?: "",
                    city = doc.getString("city") ?: "",
                    contactNumber = doc.getString("contactNumber") ?: "",
                    requestType = doc.getString("requestType") ?: "blood",
                    isEmergency = doc.getBoolean("isEmergency") ?: false,
                    status = doc.getString("status") ?: "pending",
                    createdBy = doc.getString("createdBy") ?: "",
                    createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0
                )
            }

        } catch (e: Exception) {
            Log.e("FIREBASE_ERROR", e.message ?: "")
            emptyList()
        }
    }


    override suspend fun getAllRequests(): List<RequestModel> {

        val result = db.collection("requests")
            .get()
            .await()

        return result.map { it.toModel() }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toModel(): RequestModel {
        return RequestModel(
            patientName = getString("patientName") ?: "",
            bloodGroup = getString("bloodGroup") ?: "",
            unitsRequired = getString("unitsRequired") ?: "",
            hospitalName = getString("hospitalName") ?: "",
            city = getString("city") ?: "",
            contactNumber = getString("contactNumber") ?: "",
            requestType = getString("requestType") ?: "blood",
            isEmergency = getBoolean("isEmergency") ?: false
        )
    }
}