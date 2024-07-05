package com.familylocation.mobiletracker.callLocatorModule.modalClass

import com.google.gson.annotations.SerializedName


data class SendFriendRequestResponse(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: FriendRequestData? = null,
)

data class FriendRequestData(
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("FromNumber")
    val fromNumber: String? = null,
    @SerializedName("ToNumber")
    val toNumber: String? = null,
)