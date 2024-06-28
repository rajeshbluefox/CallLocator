package com.familylocation.mobiletracker.loginModule.modalClass

import com.google.gson.annotations.SerializedName

data class RegisterUserData(
    @SerializedName("PhoneNumber")
    var phoneNumber: String? = null
)