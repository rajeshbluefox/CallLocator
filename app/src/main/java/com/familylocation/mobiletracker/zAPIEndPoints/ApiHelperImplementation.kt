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
import javax.inject.Inject


class ApiHelperImplementation @Inject constructor(private val apiService: ApiInterface) :
    ApiHelper {
    override suspend fun getThemes(): GetThemesResponse {
        return apiService.getThemes()
    }

    override suspend fun getUserInfo(phoneNumber: String): GetUserInfoResponse {
        return apiService.getUserInfo(phoneNumber)
    }

    override suspend fun postNewUser(userInfoData: UserInfoData): RegisterUserResponse {
        return apiService.postNewUser(
            userInfoData.phoneNumber!!,
            userInfoData.password!!,
            userInfoData.fCMToken!!,
            userInfoData.longitude!!,
            userInfoData.latitude!!
        )
    }

    override suspend fun sendFriendRequest(
        fromNumber: String,
        toNumber: String
    ): SendFriendRequestResponse {
        return apiService.sendFriendRequest(fromNumber, toNumber)
    }

    override suspend fun getFriendRequests(
        phoneNumber: String,
        requestType: String
    ): GetFriendRequestsResponse {
        return apiService.getFriendRequests(
            phoneNumber, requestType
        )
    }

    override suspend fun acceptFriendRequest(
        fromNumber: String,
        toNumber: String,
        requestStatus: String
    ): RespondFriendRequestResponse {
        return apiService.acceptFriendRequest(fromNumber, toNumber, requestStatus)
    }

    override suspend fun getMyFriends(fromNumber: String): GetMyFriendsResponse {
        return apiService.getMyFriends(fromNumber)
    }

    override suspend fun updateLocation(
        phoneNumber: String,
        lat: String,
        lng: String
    ): UpdateLocationResponse {
        return apiService.updateLocation(phoneNumber,lat,lng)
    }

}