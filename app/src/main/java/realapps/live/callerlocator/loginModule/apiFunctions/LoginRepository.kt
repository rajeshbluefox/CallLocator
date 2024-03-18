package realapps.live.callerlocator.loginModule.apiFunctions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import realapps.live.callerlocator.loginModule.modalClass.GetUserInfoResponse
import realapps.live.callerlocator.loginModule.modalClass.RegisterUserResponse
import realapps.live.callerlocator.loginModule.modalClass.UserInfoData
import realapps.live.callerlocator.zAPIEndPoints.ApiHelper
import javax.inject.Inject


class LoginRepository @Inject constructor(
    private val apiHelper: ApiHelper
) {
    private var getUserInfoResponse = GetUserInfoResponse()

    suspend fun getUserInfo(phoneNumber: String): GetUserInfoResponse {
        try {
            withContext(Dispatchers.IO) {
                getUserInfoResponse = apiHelper.getUserInfo(phoneNumber)
            }
        } catch (_: Exception) {
        }
        return getUserInfoResponse
    }

    private var registerUserResponse = RegisterUserResponse()

    suspend fun postNewUser(userInfoData: UserInfoData): RegisterUserResponse {
        try {
            withContext(Dispatchers.IO) {
                registerUserResponse = apiHelper.postNewUser(userInfoData)
            }
        } catch (_: Exception) {
        }
        return registerUserResponse
    }

}