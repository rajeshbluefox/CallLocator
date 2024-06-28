package com.familylocation.mobiletracker.callThemesModule.modalClass

import com.google.gson.annotations.SerializedName


data class GetThemesResponse(
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: List<ThemesData?>? = null,

    )

data class ThemesData(

    @field:SerializedName("Id")
    var id: String? = null,
    @field:SerializedName("Theme")
    val themeName: String? = null,
    @field:SerializedName("ThumbnailURL")
    val thumbNail: String? = null,
    @field:SerializedName("ThemeURL")
    val theme: String? = null,
)