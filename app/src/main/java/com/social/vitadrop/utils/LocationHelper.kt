package com.social.vitadrop.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class LocationHelper(
    private val context: Context
) {

    fun hasLocationPermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onSuccess: (
            latitude: Double,
            longitude: Double
        ) -> Unit,
        onFailure: (Exception?) -> Unit
    ) {

        if (!hasLocationPermission()) {

            onFailure(
                Exception("Location permission not granted")
            )

            return
        }

        val fusedLocationClient =
            LocationServices
                .getFusedLocationProviderClient(context)

        fusedLocationClient
            .lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    onSuccess(
                        location.latitude,
                        location.longitude
                    )

                } else {

                    onFailure(
                        Exception("Location unavailable")
                    )
                }
            }
            .addOnFailureListener {

                onFailure(it)
            }
    }
}