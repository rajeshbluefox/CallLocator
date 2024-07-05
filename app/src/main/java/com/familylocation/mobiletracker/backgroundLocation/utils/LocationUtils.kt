package com.familylocation.mobiletracker.backgroundLocation.utils

import android.content.Context
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat

fun Context.isLocationEnabled(): Boolean {
    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}