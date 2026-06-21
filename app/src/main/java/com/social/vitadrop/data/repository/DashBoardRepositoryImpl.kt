package com.social.vitadrop.data.repository
/*
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.RequestModel
import kotlinx.coroutines.tasks.await

class DashboardRepositoryImpl : DashboardRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * ============================================================
     * DONOR COUNT
     * ============================================================
     */
    override suspend fun getDonorCount(): Int {

        return try {

            // =========================
            // MODIFICATION:
            // Added detailed logs
            // =========================
            Log.d("FIREBASE_DEBUG", "Fetching donor count...")

            val result = db.collection("donors")
                .get()
                .await()

            Log.d("FIREBASE_DEBUG", "Donors Count: ${result.size()}")

            result.size()

        } catch (e: Exception) {

            // =========================
            // MODIFICATION:
            // Improved error logging
            // =========================
            Log.e(
                "FIREBASE_ERROR",
                "Error fetching donors: ${e.message}",
                e
            )

            0
        }
    }

    /**
     * ============================================================
     * HOSPITAL COUNT
     * ============================================================
     */
    override suspend fun getHospitalCount(): Int {

        return try {

            Log.d("FIREBASE_DEBUG", "Fetching hospital count...")

            val result = db.collection("hospitals")
                .get()
                .await()

            Log.d("FIREBASE_DEBUG", "Hospitals Count: ${result.size()}")

            result.size()

        } catch (e: Exception) {

            Log.e(
                "FIREBASE_ERROR",
                "Error fetching hospitals: ${e.message}",
                e
            )

            0
        }
    }

    /**
     * ============================================================
     * REQUEST COUNT
     * ============================================================
     */
    override suspend fun getRequestCount(): Int {

        return try {

            Log.d("FIREBASE_DEBUG", "Fetching request count...")

            val result = db.collection("requests")
                .get()
                .await()

            Log.d("FIREBASE_DEBUG", "Requests Count: ${result.size()}")

            result.size()

        } catch (e: Exception) {

            Log.e(
                "FIREBASE_ERROR",
                "Error fetching requests: ${e.message}",
                e
            )

            0
        }
    }

    /**
     * ============================================================
     * EMERGENCY REQUESTS
     * ============================================================
     */
    override suspend fun getEmergencyRequests(): List<RequestModel> {

        return try {

            Log.d("FIREBASE_DEBUG", "Fetching emergency requests...")

            /**
             * =====================================================
             * MODIFICATION:
             * Fixed inconsistent field name issue.
             *
             * OLD:
             * whereEqualTo("isEmergency", true)
             *
             * NEW:
             * whereEqualTo("emergency", true)
             *
             * IMPORTANT:
             * Make sure your Firebase field name is:
             * emergency : true
             * =====================================================
             */
            val result = db.collection("requests")
                .whereEqualTo("emergency", true)
                .get()
                .await()

            Log.d(
                "FIREBASE_DEBUG",
                "Emergency Requests Count: ${result.size()}"
            )

            result.documents.forEach {

                Log.d(
                    "DEBUG_DOC",
                    "Emergency Request Data: ${it.data}"
                )
            }

            result.documents.map { doc ->

                doc.toRequestModel()
            }

        } catch (e: Exception) {

            Log.e(
                "FIREBASE_ERROR",
                "Error fetching emergency requests: ${e.message}",
                e
            )

            emptyList()
        }
    }

    /**
     * ============================================================
     * ALL REQUESTS
     * ============================================================
     */
    override suspend fun getAllRequests(): List<RequestModel> {

        return try {

            Log.d("FIREBASE_DEBUG", "Fetching all requests...")

            val result = db.collection("requests")
                .get()
                .await()

            Log.d(
                "FIREBASE_DEBUG",
                "Total Requests Fetched: ${result.size()}"
            )

            result.documents.map { doc ->

                doc.toRequestModel()
            }

        } catch (e: Exception) {

            /**
             * =====================================================
             * MODIFICATION:
             * Added try-catch.
             * Earlier this method could crash the app.
             * =====================================================
             */
            Log.e(
                "FIREBASE_ERROR",
                "Error fetching all requests: ${e.message}",
                e
            )

            emptyList()
        }
    }

    /**
     * ============================================================
     * DOCUMENT TO MODEL CONVERTER
     * ============================================================
     */
    private fun DocumentSnapshot.toRequestModel(): RequestModel {

        /**
         * =====================================================
         * MODIFICATION:
         * Safer location parsing.
         * Prevents ClassCastException.
         * =====================================================
         */
        val locationMap = get("location") as? Map<*, *>

        val latitude = (
                locationMap?.get("latitude") as? Number
                )?.toDouble() ?: 0.0

        val longitude = (
                locationMap?.get("longitude") as? Number
                )?.toDouble() ?: 0.0

        /**
         * =====================================================
         * MODIFICATION:
         * Fixed wrong getLong() issue.
         *
         * IMPORTANT:
         * Removed:
         * import java.lang.Long.getLong
         *
         * Now using Firestore getLong correctly.
         * =====================================================
         */
        val unitsRequiredValue =
            getLong("unitsRequired")?.toString() ?: ""

        return RequestModel(

            /**
             * =================================================
             * REQUEST INFO
             * =================================================
             */
            requestId = getString("requestId") ?: id,

            requestedBy = getString("requestedBy") ?: "",

            hospitalId = getString("hospitalId") ?: "",

            /**
             * =================================================
             * PATIENT INFO
             * =================================================
             */
            patientName = getString("patientName") ?: "",

            /**
             * =================================================
             * BLOOD INFO
             * =================================================
             */
            bloodGroup = getString("bloodGroup") ?: "",

            unitsRequired = unitsRequiredValue,

            /**
             * =================================================
             * CONTACT INFO
             * =================================================
             */
            contactPerson = getString("contactPerson") ?: "",

            contactPhone = getString("contactPhone") ?: "",

            /**
             * =================================================
             * HOSPITAL INFO
             * =================================================
             */
            hospitalName = getString("hospitalName") ?: "",

            city = getString("city") ?: "",

            /**
             * =================================================
             * LOCATION
             * =================================================
             */
            location = mapOf(

                "latitude" to latitude,

                "longitude" to longitude
            ),

            /**
             * =================================================
             * REQUEST DETAILS
             * =================================================
             */
            urgency = getString("urgency") ?: "medium",

            status = getString("status") ?: "pending",

            description = getString("description") ?: "",

            /**
             * =================================================
             * DONOR TRACKING
             * =================================================
             */
            acceptedDonors =
                get("acceptedDonors") as? List<String>
                    ?: emptyList(),

            rejectedDonors =
                get("rejectedDonors") as? List<String>
                    ?: emptyList(),

            completedBy =
                get("completedBy") as? List<String>
                    ?: emptyList(),

            /**
             * =================================================
             * EMERGENCY
             * =================================================
             */
            emergency = getBoolean("emergency") ?: false,

            /**
             * =================================================
             * TIMESTAMP
             * =================================================
             */
            createdAt = getTimestamp("createdAt")
                ?.toDate()
                ?.time
        )
    }
}

 */
//===================================================

// Updated DashBoardImplementation.

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.utils.FirebaseProvider
import kotlinx.coroutines.tasks.await

class DashboardRepositoryImpl : DashboardRepository {

    private val db = FirebaseFirestore.getInstance()




     // DONOR COUNT

    override suspend fun getDonorCount(): Int {

        return try {

            Log.d("FIREBASE_DEBUG", "Fetching donor count...")

            val result = db.collection("donors")
                .get()
                .await()

            Log.d(
                "FIREBASE_DEBUG",
                "Donors Count: ${result.size()}"
            )

            result.size()

        } catch (e: Exception) {

            Log.e(
                "FIREBASE_ERROR",
                "Error fetching donors: ${e.message}",
                e
            )

            0
        }
    }


     //HOSPITAL COUNT

    override suspend fun getHospitalCount(): Int {

        return try {

            Log.d("FIREBASE_DEBUG", "Fetching hospital count...")

            val result = db.collection("hospitals")
                .get()
                .await()

            Log.d(
                "FIREBASE_DEBUG",
                "Hospitals Count: ${result.size()}"
            )

            result.size()

        } catch (e: Exception) {

            Log.e(
                "FIREBASE_ERROR",
                "Error fetching hospitals: ${e.message}",
                e
            )

            0
        }
    }


     //REQUEST COUNT

    override suspend fun getRequestCount(): Int {

        return try {

            Log.d("FIREBASE_DEBUG", "Fetching request count...")

            val result = db.collection("requests")
                .get()
                .await()

            Log.d(
                "FIREBASE_DEBUG",
                "Requests Count: ${result.size()}"
            )

            result.size()

        } catch (e: Exception) {

            Log.e(
                "FIREBASE_ERROR",
                "Error fetching requests: ${e.message}",
                e
            )

            0
        }
    }


     // EMERGENCY REQUESTS

    override suspend fun getEmergencyRequests(): List<RequestModel> {

        return try {

            Log.d(
                "FIREBASE_DEBUG",
                "Fetching emergency requests..."
            )


            val result = db.collection("requests")
                .whereEqualTo("emergency", true)
                .get()
                .await()

            Log.d(
                "FIREBASE_DEBUG",
                "Emergency Requests Count: ${result.size()}"
            )


            result.documents.mapNotNull { doc ->

                try {

                    Log.d(
                        "EMERGENCY_REQUEST_DOC",
                        "Document Data = ${doc.data}"
                    )

                    doc.toRequestModel()

                } catch (e: Exception) {

                    Log.e(
                        "EMERGENCY_PARSE_ERROR",
                        "Failed document: ${doc.id}",
                        e
                    )

                    null
                }
            }

        } catch (e: Exception) {

            Log.e(
                "FIREBASE_ERROR",
                "Error fetching emergency requests: ${e.message}",
                e
            )

            emptyList()
        }
    }

    /**
     * ALL REQUESTS
     */
    override suspend fun getAllRequests(): List<RequestModel> {

        return try {

            Log.d(
                "FIREBASE_DEBUG",
                "Fetching all requests..."
            )

            val result = db.collection("requests")
                .get()
                .await()

            Log.d(
                "FIREBASE_DEBUG",
                "Total Requests Fetched: ${result.size()}"
            )

            /**
             * MODIFICATION:
             * Added safe parsing using mapNotNull.
             * Prevents one bad document from crashing all requests.
             */
            result.documents.mapNotNull { doc ->

                try {

                    Log.d(
                        "REQUEST_DOC",
                        "Document Data = ${doc.data}"
                    )

                    doc.toRequestModel()

                } catch (e: Exception) {

                    Log.e(
                        "REQUEST_PARSE_ERROR",
                        "Failed document: ${doc.id}",
                        e
                    )

                    null
                }
            }

        } catch (e: Exception) {

            Log.e(
                "FIREBASE_ERROR",
                "Error fetching all requests: ${e.message}",
                e
            )

            emptyList()
        }
    }


     // DOCUMENT TO MODEL CONVERTER

    private fun DocumentSnapshot.toRequestModel(): RequestModel {

        /**
         * ====================================================
         * MODIFICATION:
         * Safer location parsing.
         * Prevents ClassCastException.
         * ====================================================
         */
        val locationMap = get("location") as? Map<*, *>

        val latitude = (
                locationMap?.get("latitude") as? Number
                )?.toDouble() ?: 0.0

        val longitude = (
                locationMap?.get("longitude") as? Number
                )?.toDouble() ?: 0.0

        /**
         * ====================================================
         * MODIFICATION:
         * OLD:
         * getLong("unitsRequired")?.toString() ?: ""
         * NEW:
         * Handles BOTH:
         * - Number
         * - String
         * Prevents Firebase type mismatch crash.
         * ====================================================
         */
        val unitsRequiredValue = when (val value = get("unitsRequired")) {

            is Long -> value

            is Int -> value.toLong()

            is Double -> value.toLong()

            is String -> value.toLongOrNull() ?: 0

            else -> 0
        }

        return RequestModel(


             //REQUEST INFO

            requestId = getString("requestId") ?: id,

            requestedBy = getString("requestedBy") ?: "",

            hospitalId = getString("hospitalId") ?: "",


             // PATIENT INFO

            patientName = getString("patientName") ?: "",


             // BLOOD INFORMATION

            bloodGroup = getString("bloodGroup") ?: "",


             // MODIFICATION:
             // unitsRequired now uses Long.

            unitsRequired = unitsRequiredValue,


             // CONTACT INFORMATION

            contactPerson = getString("contactPerson") ?: "",

            contactPhone = getString("contactPhone") ?: "",

          // Hospital INfo
            hospitalName = getString("hospitalName") ?: "",

            city = getString("city") ?: "",


             // LOCATION Information

            location = mapOf(

                "latitude" to latitude,

                "longitude" to longitude
            ),


            urgency = getString("urgency") ?: "medium",

            status = getString("status") ?: "pending",

            description = getString("description") ?: "",


             // DONOR TRACKING

            acceptedDonors =
                get("acceptedDonors") as? List<String>
                    ?: emptyList(),

            rejectedDonors =
                get("rejectedDonors") as? List<String>
                    ?: emptyList(),

            completedBy =
                get("completedBy") as? List<String>
                    ?: emptyList(),


             // EMERGENCY

            emergency = getBoolean("emergency") ?: false,

             //* TIMESTAMP

            createdAt =
                getTimestamp("createdAt")
        )
    }
}