package com.familylocation.mobiletracker.callLocatorModule.modalClass

import com.google.gson.annotations.SerializedName

data class GetFriendRequestsResponse(

	@field:SerializedName("data")
	val data: List<GetFriendRequestDataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class GetFriendRequestDataItem(

	@field:SerializedName("ToNumber")
	val toNumber: String? = null,

	@field:SerializedName("RequestId")
	val requestId: String? = null,

	@field:SerializedName("FromNumber")
	val fromNumber: String? = null,

	@field:SerializedName("RequestStatus")
	val requestStatus: String? = null
)
