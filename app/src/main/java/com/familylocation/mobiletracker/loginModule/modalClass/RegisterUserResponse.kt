package com.familylocation.mobiletracker.loginModule.modalClass

import com.google.gson.annotations.SerializedName


data class RegisterUserResponse(
    @SerializedName("data")
    val data: RegisterUserData? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status")
    val status: Int? = null
)

