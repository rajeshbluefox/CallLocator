package com.familylocation.mobiletracker.loginModule.modalClass

import com.google.gson.annotations.SerializedName

data class GetUserInfoResponse(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: UserInfoData? = null,
)



