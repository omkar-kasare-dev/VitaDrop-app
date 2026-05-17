package com.social.vitadrop.data.repository


/*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

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

    }

 */

// Modified Request Repository File;


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.RequestRepository
import kotlinx.coroutines.tasks.await

class RequestRepositoryImpl : RequestRepository {

    private val db = FirebaseFirestore.getInstance()

    private val auth = FirebaseAuth.getInstance()

    override suspend fun createRequest(request: RequestModel) {

        val userId = auth.currentUser?.uid ?: ""

        // CREATE NEW DOCUMENT REFERENCE
        val requestRef = db.collection("requests").document()

        val requestId = requestRef.id

        val data = hashMapOf(

            // REQUEST INFO
            "requestId" to requestId,

            "requestedBy" to request.requestedBy,

            "hospitalId" to request.hospitalId,

            // PATIENT INFO
            "patientName" to request.patientName,

            // BLOOD INFO
            "bloodGroup" to request.bloodGroup,

            "unitsRequired" to request.unitsRequired,

            // CONTACT INFO
            "contactPerson" to request.contactPerson,

            "contactPhone" to request.contactPhone,

            // HOSPITAL INFO
            "hospitalName" to request.hospitalName,

            "city" to request.city,

            // LOCATION
            "location" to hashMapOf(

                "latitude" to (
                        request.location["latitude"] ?: 0.0
                        ),

                "longitude" to (
                        request.location["longitude"] ?: 0.0
                        )
            ),

            // REQUEST DETAILS
            "urgency" to request.urgency,

            "status" to request.status,

            "description" to request.description,

            // DONOR TRACKING
            "acceptedDonors" to request.acceptedDonors,

            "rejectedDonors" to request.rejectedDonors,

            "completedBy" to request.completedBy,

            // EMERGENCY
            "emergency" to request.emergency,

            // SYSTEM
            "createdBy" to userId,

            "createdAt" to FieldValue.serverTimestamp()
        )

        requestRef
            .set(data)
            .await()
    }
}
