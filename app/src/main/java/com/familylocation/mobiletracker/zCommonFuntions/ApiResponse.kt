package com.familylocation.mobiletracker.zCommonFuntions

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: NotificationData?
)

data class NotificationData(
    @SerializedName("notificationId")
    val notificationId: String
)