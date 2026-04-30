package com.social.vitadrop.data.repository



import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.RequestRepository
import kotlinx.coroutines.tasks.await

class RequestRepositoryImpl : RequestRepository {

        private val db = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()

        override suspend fun createRequest(request: RequestModel) {

            val userId = auth.currentUser?.uid ?: ""

            val data = hashMapOf(
                "patientName" to request.patientName,
                "bloodGroup" to request.bloodGroup,
                "unitsRequired" to request.unitsRequired,
                "hospitalName" to request.hospitalName,
                "city" to request.city,
                "contactNumber" to request.contactNumber,
                "requestType" to request.requestType,
                "isEmergency" to request.isEmergency,
                "status" to "pending",
                "createdBy" to userId,
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("requests")
                .add(data)
                .await()
        }


    //


    }
