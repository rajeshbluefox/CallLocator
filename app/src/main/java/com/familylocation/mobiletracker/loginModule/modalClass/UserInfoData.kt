package com.familylocation.mobiletracker.loginModule.modalClass

import com.google.gson.annotations.SerializedName

data class UserInfoData(
    @SerializedName("FCMToken")
    var fCMToken: String? = null,
    @SerializedName("latitude")
    var latitude: String? = null,
    @SerializedName("PhoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("status")
    val locationTimeStamp: String? = null,
    @SerializedName("Password")
    var password: String? = null,
    @SerializedName("longitude")
    var longitude: String? = null
)