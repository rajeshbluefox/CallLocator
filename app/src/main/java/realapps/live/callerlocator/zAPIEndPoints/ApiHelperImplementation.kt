package realapps.live.callerlocator.zAPIEndPoints

import realapps.live.callerlocator.callLocatorModule.modalClass.GetFriendRequestsResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.GetMyFriendsResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.RespondFriendRequestResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.SendFriendRequestResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.UpdateLocationResponse
import realapps.live.callerlocator.callThemesModule.modalClass.GetThemesResponse
import realapps.live.callerlocator.loginModule.modalClass.GetUserInfoResponse
import realapps.live.callerlocator.loginModule.modalClass.RegisterUserResponse
import realapps.live.callerlocator.loginModule.modalClass.UserInfoData
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