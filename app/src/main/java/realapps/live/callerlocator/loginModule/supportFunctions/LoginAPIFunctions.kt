package realapps.live.callerlocator.loginModule.supportFunctions

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import realapps.live.callerlocator.loginModule.apiFunctions.LoginViewModel
import realapps.live.callerlocator.loginModule.modalClass.UserInfoData
import realapps.live.callerlocator.loginModule.modalClass.UserInfoDataClass
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.LoginData


class LoginAPIFunctions(
    val context: Context,
    val activity: Activity,
    private val lifeCycleOwner: LifecycleOwner,
    private var loginViewModel: LoginViewModel,
    private val findUserExistenceObserverCallBack: (status: Boolean) -> Unit,
    private val postNewUserObserverCallBack: () -> Unit
) {
    fun findUserExistence(phoneNumber: String) {
        loginViewModel.resetUserInfo()
        loginViewModel.getUserInfo(phoneNumber)
        findUserExistenceObserver()
    }

    fun postNewUser(phoneNumber: String, password: String) {
        val userInfoData = UserInfoData()
        userInfoData.phoneNumber = phoneNumber
        userInfoData.password = password
        userInfoData.fCMToken = "EMPTY"
        userInfoData.latitude = "EMPTY"
        userInfoData.longitude = "EMPTY"

        loginViewModel.postNewUser(userInfoData)
        postNewUserObserver()
    }

    private fun gotoHomeScreen() {
        CallIntent.goToHomeActivity(context, true, activity)
    }

    private fun findUserExistenceObserver() {
        Log.e("Test", "45")
        loginViewModel.getUserInfoResponse().observe(lifeCycleOwner) {

            if (it.status != -1) {
                Log.e("Test", "49")
                if (it.status == 200) {
                    UserInfoDataClass.userData = it.data!!

                    findUserExistenceObserverCallBack.invoke(true)
//                    loginUI.showPasswordLayout(true)
                } else {
                    findUserExistenceObserverCallBack.invoke(false)
//                    loginUI.showPasswordLayout(false)
                }
            }
        }
    }

    private fun postNewUserObserver() {
        loginViewModel.postNewUserResponse().observe(lifeCycleOwner) {
            if (it.status != null) {
                if (it.status == 200) {
                    LoginData.saveUserPhone(context, it.data?.phoneNumber!!)
                    UtilFunctions.showToast(context, "Registered Successfully")
                    postNewUserObserverCallBack.invoke()
//                    gotoHomeScreen()
                }
            }
        }
    }
}