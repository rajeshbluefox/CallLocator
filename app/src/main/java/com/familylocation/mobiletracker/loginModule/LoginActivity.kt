package com.familylocation.mobiletracker.loginModule

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import com.familylocation.mobiletracker.databinding.ActivityLoginBinding
import com.familylocation.mobiletracker.loginModule.apiFunctions.LoginViewModel
import com.familylocation.mobiletracker.loginModule.modalClass.UserInfoDataClass
import com.familylocation.mobiletracker.loginModule.supportFunctions.LoginAPIFunctions
import com.familylocation.mobiletracker.loginModule.supportFunctions.LoginUI
import com.familylocation.mobiletracker.zCommonFuntions.CallIntent
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.LoginData
import com.hbb20.CountryCodePicker

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

        StatusBarUtils.transparentStatusBar(this)


        initViews()
        setOnClickListeners()

    }



    private fun initViews() {
        loginUI = LoginUI(this, binding, ::findUserExistence, ::gotoHomeScreen, ::postNewUser)
        loginAPIFunctions = LoginAPIFunctions(
            this,
            this,
            this,
            loginViewModel,
            ::showPasswordLayout,
            ::gotoHomeScreen
        )
    }

    private fun showPasswordLayout(status: Boolean) {
        if (status)
            loginUI.checkPassword(UserInfoDataClass.userData.password!!)
        else
        {
            loginUI.showPB(false)
            UtilFunctions.showToast(this,"Account doesn't exists")
        }
//        loginUI.showPasswordLayout(status)
    }

    private fun setOnClickListeners() {
        binding.btLogin.setOnClickListener {

            when (binding.btLogin.text) {
                "SignIn" -> {
                    loginUI.checkFields()
                }

                "Register" -> {
                    loginUI.createPassword()
                }

                else -> {
                }
            }

        }

        binding.showRegister.setOnClickListener {
            val btText = binding.showRegister.text.toString()

            if(btText=="Register")
            {
                binding.etCPassword.visibility=View.VISIBLE
                binding.tvDes.text = "Already have an account?"
                binding.showRegister.text = "SignIn"

                binding.textView2.text="Register"
                binding.btLogin.text="Register"
            }else{
                binding.etCPassword.visibility=View.GONE
                binding.tvDes.text = "Don't have an account?"
                binding.showRegister.text = "Register"

                binding.textView2.text="SignIn"
                binding.btLogin.text="SignIn"
            }
        }
    }


    private fun findUserExistence(phoneNumber: String) {
        loginUI.showPB(true)
        loginAPIFunctions.findUserExistence(phoneNumber)
//        loginViewModel.getUserInfo(phoneNumber)
//        findUserExistenceObserver()
    }

    private fun postNewUser(phoneNumber: String, password: String) {
        loginUI.showPB(true)
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
        loginUI.showPB(false)
        LoginData.saveUserLoginStatus(this, true)
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