package realapps.live.callerlocator.zAPIEndPoints

import realapps.live.callerlocator.callLocatorModule.modalClass.GetFriendRequestsResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.GetMyFriendsResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.RespondFriendRequestResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.SendFriendRequestResponse
import realapps.live.callerlocator.loginModule.modalClass.GetUserInfoResponse
import realapps.live.callerlocator.loginModule.modalClass.RegisterUserResponse
import realapps.live.callerlocator.loginModule.modalClass.UserInfoData

interface ApiHelper {

    suspend fun getUserInfo(phoneNumber: String): GetUserInfoResponse

    suspend fun postNewUser(userInfoData: UserInfoData): RegisterUserResponse

    suspend fun sendFriendRequest(fromNumber: String,toNumber: String): SendFriendRequestResponse

    suspend fun getFriendRequests(phoneNumber: String,requestType: String): GetFriendRequestsResponse

    suspend fun acceptFriendRequest(fromNumber: String,toNumber: String,requestStatus: String): RespondFriendRequestResponse

    suspend fun getMyFriends(fromNumber: String): GetMyFriendsResponse


}