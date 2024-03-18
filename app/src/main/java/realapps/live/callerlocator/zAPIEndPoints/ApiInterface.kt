package realapps.live.callerlocator.zAPIEndPoints

import realapps.live.callerlocator.callLocatorModule.modalClass.GetFriendRequestsResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.SendFriendRequestResponse
import realapps.live.callerlocator.loginModule.modalClass.GetUserInfoResponse
import realapps.live.callerlocator.loginModule.modalClass.RegisterUserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {


    @FormUrlEncoded
    @POST("get_users.php")
    suspend fun getUserInfo(
        @Field("PhoneNumber") phoneNumber: String
    ): GetUserInfoResponse

    @FormUrlEncoded
    @POST("user_insert.php")
    suspend fun postNewUser(
        @Field("PhoneNumber") phoneNumber: String,
        @Field("Password") password: String,
        @Field("FCMToken") fCMToken: String,
        @Field("Latitude") latitude: String,
        @Field("Longitude") longitude: String
    ): RegisterUserResponse

    @FormUrlEncoded
    @POST("FriendRequests_insert.php")
    suspend fun sendFriendRequest(
        @Field("FromNumber") fromNumber: String,
        @Field("ToNumber") toNumber: String
    ): SendFriendRequestResponse

    @FormUrlEncoded
    @POST("Get_FriendRequests.php")
    suspend fun getFriendRequests(
        @Field("PhoneNumber") phoneNumber: String,
        @Field("RequestType") requestType: String
    ): GetFriendRequestsResponse
}