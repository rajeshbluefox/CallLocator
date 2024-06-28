package com.familylocation.mobiletracker.loginModule.apiFunctions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.familylocation.mobiletracker.loginModule.modalClass.GetUserInfoResponse
import com.familylocation.mobiletracker.loginModule.modalClass.RegisterUserResponse
import com.familylocation.mobiletracker.loginModule.modalClass.UserInfoData
import com.familylocation.mobiletracker.zAPIEndPoints.ApiHelper
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