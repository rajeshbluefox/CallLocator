package realapps.live.callerlocator.callLocatorModule.apiFunctions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import realapps.live.callerlocator.callLocatorModule.modalClass.GetFriendRequestsResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.GetMyFriendsResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.RespondFriendRequestResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.SendFriendRequestResponse
import realapps.live.callerlocator.zAPIEndPoints.ApiHelper
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
}