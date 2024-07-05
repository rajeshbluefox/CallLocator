package com.familylocation.mobiletracker.zAPIEndPoints

import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetFriendRequestsResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetMyFriendsResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.RespondFriendRequestResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.SendFriendRequestResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.UpdateLocationResponse
import com.familylocation.mobiletracker.callThemesModule.modalClass.GetThemesResponse
import com.familylocation.mobiletracker.loginModule.modalClass.GetUserInfoResponse
import com.familylocation.mobiletracker.loginModule.modalClass.RegisterUserResponse
import com.familylocation.mobiletracker.loginModule.modalClass.UserInfoData

interface ApiHelper {

    suspend fun getThemes(): GetThemesResponse

    suspend fun getUserInfo(phoneNumber: String): GetUserInfoResponse

    suspend fun postNewUser(userInfoData: UserInfoData): RegisterUserResponse

    suspend fun sendFriendRequest(fromNumber: String,toNumber: String): SendFriendRequestResponse

    suspend fun getFriendRequests(phoneNumber: String,requestType: String): GetFriendRequestsResponse

    suspend fun acceptFriendRequest(fromNumber: String,toNumber: String,requestStatus: String): RespondFriendRequestResponse

    suspend fun getMyFriends(fromNumber: String): GetMyFriendsResponse

    suspend fun updateLocation(phoneNumber: String,lat: String,lng: String): UpdateLocationResponse


}