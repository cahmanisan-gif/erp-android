package com.rajavavapor.app.util

import android.annotation.SuppressLint
import android.app.Activity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float
)

class LocationHelper(activity: Activity) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(activity)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult? {
        return suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }

            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(LocationResult(location.latitude, location.longitude, location.accuracy))
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }
}
