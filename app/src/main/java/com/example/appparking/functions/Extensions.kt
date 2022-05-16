package com.example.appparking.functions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.telephony.CellLocation.requestLocationUpdate
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

fun Activity.getCurrentLocation(unit : (location: Location?) -> Unit) {
    // checking location permission
    if (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return
    }

    LocationServices.getFusedLocationProviderClient(this).lastLocation
        .addOnCompleteListener { task ->
            if(task.result == null) {
                requestLocationUpdate(unit)
            }
            else
                unit.invoke(task.result)
        }
        .addOnFailureListener {
            Toast.makeText(this, "Failed on getting current location",
                Toast.LENGTH_SHORT).show()
        }
}

@SuppressLint("MissingPermission")
private fun Activity.requestLocationUpdate(unit : (location: Location?) -> Unit) {
    with(LocationRequest.create()) {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 0
        fastestInterval = 0
        numUpdates = 2

        LocationServices.getFusedLocationProviderClient(this@requestLocationUpdate).requestLocationUpdates(this, object: LocationCallback() {

            override fun onLocationResult(p0: LocationResult) {
                unit.invoke(p0.lastLocation)
            }

            override fun onLocationAvailability(p0: LocationAvailability) { super.onLocationAvailability(p0) }
        },
            Looper.myLooper()!!
        )
    }
}