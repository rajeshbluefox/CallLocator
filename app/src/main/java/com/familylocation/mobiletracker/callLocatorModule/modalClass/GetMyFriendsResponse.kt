package com.familylocation.mobiletracker.callLocatorModule.modalClass

import com.google.gson.annotations.SerializedName

data class GetMyFriendsResponse(

	@field:SerializedName("data")
	val data: List<MyFriendsDataItem?>? = null,

	@field:SerializedName("all_friend_requests")
	val allFriend_Requests: List<MyFriendsDataItem?>? = null,


	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class MyFriendsDataItem(

	@field:SerializedName("friend_name")
	var friendName: String? = null,

	@field:SerializedName("friend_number")
	var friendNumber: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("LocationTimeStamp")
	val locationTimeStamp: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)
