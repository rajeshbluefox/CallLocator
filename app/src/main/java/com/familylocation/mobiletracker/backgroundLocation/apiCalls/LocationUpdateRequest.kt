package com.familylocation.mobiletracker.backgroundLocation.apiCalls

data class LocationUpdateRequest(
    val phoneNumber: String,
    val lat: String,
    val lng: String
)