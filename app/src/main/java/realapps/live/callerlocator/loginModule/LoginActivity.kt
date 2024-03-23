package realapps.live.callerlocator.loginModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.databinding.ActivityLoginBinding
import realapps.live.callerlocator.loginModule.apiFunctions.LoginViewModel
import realapps.live.callerlocator.loginModule.modalClass.UserInfoDataClass
import realapps.live.callerlocator.loginModule.supportFunctions.LoginAPIFunctions
import realapps.live.callerlocator.loginModule.supportFunctions.LoginUI
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.LoginData

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var loginUI: LoginUI

    private lateinit var loginAPIFunctions: LoginAPIFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        setContentView(binding.root)

        val window = window
        UtilFunctions.setStatusBarIconColor(window, true) // Set status bar icons to light


        initViews()
        setOnClickListeners()

    }

    private fun initViews() {
        loginUI = LoginUI(this, binding, ::findUserExistence, ::gotoHomeScreen, ::postNewUser)
        loginAPIFunctions = LoginAPIFunctions(this,this,this,loginViewModel,::showPasswordLayout,::gotoHomeScreen)
    }

    private fun showPasswordLayout(status: Boolean)
    {
        loginUI.showPasswordLayout(status)
    }

    private fun setOnClickListeners() {
        binding.btLogin.setOnClickListener {

            when (binding.btLogin.text) {
                "Login" -> {
                    loginUI.checkPassword(UserInfoDataClass.userData.password!!)
                }
                "Register" -> {
                    loginUI.createPassword()
                }
                else -> {
                    loginUI.checkFields()
                }
            }

        }
    }


    private fun findUserExistence(phoneNumber: String) {
        loginAPIFunctions.findUserExistence(phoneNumber)
//        loginViewModel.getUserInfo(phoneNumber)
//        findUserExistenceObserver()
    }

    private fun postNewUser(phoneNumber: String, password: String) {
        loginAPIFunctions.postNewUser(phoneNumber, password)

//        val userInfoData = userInfoData()
//        userInfoData.phoneNumber = phoneNumber
//        userInfoData.password = password
//        userInfoData.fCMToken = "EMPTY"
//        userInfoData.latitude = "EMPTY"
//        userInfoData.longitude = "EMPTY"
//
//        loginViewModel.postNewUser(userInfoData)
//        postNewUserObserver()
    }

    private fun gotoHomeScreen() {
        LoginData.saveUserLoginStatus(this,true)
        CallIntent.goToHomeActivity(this, true, this)
    }


    private fun findUserExistenceObserver() {
        loginViewModel.getUserInfoResponse().observe(this) {
            if (it.status != null) {
                if (it.status == 200) {
//    TODO                UserInfoDataClass.userData = it.data!!

                    loginUI.showPasswordLayout(true)
                } else {
                    loginUI.showPasswordLayout(false)
                }
            }
        }
    }

    private fun postNewUserObserver() {
        loginViewModel.postNewUserResponse().observe(this) {
            if (it.status != null) {
                if (it.status == 200) {
                    LoginData.saveUserPhone(this, it.data?.phoneNumber!!)
                    UtilFunctions.showToast(this, "Registered Successfully")
                    gotoHomeScreen()
                }
            }
        }
    }

}