package com.familylocation.mobiletracker.callLocatorModule.apiFunctions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetFriendRequestsResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetMyFriendsResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.RespondFriendRequestResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.SendFriendRequestResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.UpdateLocationResponse
import com.familylocation.mobiletracker.zAPIEndPoints.ApiHelper
import javax.inject.Inject


class CallLocatorRepository @Inject constructor(
    private val apiHelper: ApiHelper
) {
    private var sendFriendRequestResponse = SendFriendRequestResponse()

    suspend fun sendFriendRequest(fromNumber: String, toNumber: String): SendFriendRequestResponse {
        try {
            withContext(Dispatchers.IO) {
                sendFriendRequestResponse = apiHelper.sendFriendRequest(fromNumber, toNumber)
            }
        } catch (_: Exception) {
        }
        return sendFriendRequestResponse
    }

    private var getFriendRequestsResponse = GetFriendRequestsResponse()

    suspend fun getFriendRequests(fromNumber: String, requestType: String): GetFriendRequestsResponse {
        try {
            withContext(Dispatchers.IO) {
                getFriendRequestsResponse = apiHelper.getFriendRequests(fromNumber, requestType)
            }
        } catch (_: Exception) {
        }
        return getFriendRequestsResponse
    }

    private var respondFriendRequestResponse = RespondFriendRequestResponse()

    suspend fun acceptFriendRequest(fromNumber: String,toNumber: String,requestStatus: String): RespondFriendRequestResponse {
        try {
            withContext(Dispatchers.IO) {
                respondFriendRequestResponse = apiHelper.acceptFriendRequest(fromNumber,toNumber, requestStatus)
            }
        } catch (_: Exception) {
        }
        return respondFriendRequestResponse
    }

    private var getMyFriendsResponse = GetMyFriendsResponse()

    suspend fun getMyFriends(fromNumber: String): GetMyFriendsResponse {
        try {
            withContext(Dispatchers.IO) {
                getMyFriendsResponse = apiHelper.getMyFriends(fromNumber)
            }
        } catch (_: Exception) {
        }
        return getMyFriendsResponse
    }

    private var updateLocationResponse = UpdateLocationResponse()

    suspend fun updateLocation(phoneNumber: String,lat: String,lng: String): UpdateLocationResponse {
        try {
            withContext(Dispatchers.IO) {
                updateLocationResponse = apiHelper.updateLocation(phoneNumber, lat, lng)
            }
        } catch (_: Exception) {
        }
        return updateLocationResponse
    }
}