package com.familylocation.mobiletracker.callLocatorModule.modalClass

import com.google.gson.annotations.SerializedName



data class UpdateLocationResponse(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("message")
    val message: String? = null
)