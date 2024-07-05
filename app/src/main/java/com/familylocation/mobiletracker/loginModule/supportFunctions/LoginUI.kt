package com.familylocation.mobiletracker.loginModule.supportFunctions

import android.content.Context
import android.graphics.Region
import android.util.Log
import android.view.View
import com.familylocation.mobiletracker.databinding.ActivityLoginBinding
import com.familylocation.mobiletracker.loginModule.modalClass.UserInfoDataClass
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zCommonFuntions.ValidationUtils
import com.familylocation.mobiletracker.zSharedPreference.LoginData


class LoginUI(
    val context: Context,
    private val binding: ActivityLoginBinding,
    private val onNumberValidated: (phoneNumber: String) -> Unit,
    private val onPasswordValidated: () -> Unit,
    private val onPasswordCreated: (phoneNumber: String,password: String) -> Unit
) {

    fun checkFields() {
        val mPhoneNumber = getPhoneNumber()
//        val fullNumber = binding.countryCode.selectedCountryCode
        val onlyPhoneNumber = binding.etUserName.text.toString()

//        val mPassword = binding.etPassword.text.toString()

        if (onlyPhoneNumber.isEmpty()) {
            UtilFunctions.showToast(context, "Enter MobileNumber")
            return
        }

//        if (ValidationUtils.isValidPhoneNumber(mPhoneNumber,"")) {
//            UtilFunctions.showToast(context, "Enter Valid MobileNumber")
//            return
//        }

//        UtilFunctions.showToast(context, "LoggingIn")

        onNumberValidated.invoke(onlyPhoneNumber)
    }

    fun checkPassword(userPassword: String) {
        val mPassword = binding.etPassword.text.toString()

        if (mPassword.isEmpty()) {
            showPB(false)
            UtilFunctions.showToast(context, "Enter Password")
            return
        }

        if (mPassword.length < 6) {
            showPB(false)
            UtilFunctions.showToast(context, "Enter valid password")
            return
        }


        if (userPassword != mPassword) {
            showPB(false)
            UtilFunctions.showToast(context, "Incorrect password")
            return
        }

        //Save UserPhoneNumber via Login Flow
        LoginData.saveUserPhone(context,UserInfoDataClass.userData.phoneNumber!!)

        onPasswordValidated.invoke()
    }

    fun createPassword() {
        val mPhoneNumber = getPhoneNumber()
//        val fullNumber = binding.countryCode
        val onlyPhoneNumber = binding.etUserName.text.toString()
        val mPassword = binding.etPassword.text.toString()
        val mCPassword = binding.etCPassword.text.toString()

        Log.e("Test","Passwords - $mPassword = $mCPassword")

        if (onlyPhoneNumber.isEmpty()) {
            UtilFunctions.showToast(context, "Enter MobileNumber")
            return
        }

//        if(ValidationUtils.isValidPhoneNumber(mPhoneNumber,""))
//        {
//            UtilFunctions.showToast(context, "Enter Valid MobileNumber")
//            return
//        }

//        if (mPhoneNumber.length != 10) {
//            UtilFunctions.showToast(context, "Enter Valid MobileNumber")
//            return
//        }

        if (mPassword.isEmpty()) {
            UtilFunctions.showToast(context, "Enter Password")
            return
        }

        if (mPassword.length < 6) {
            UtilFunctions.showToast(context, "Password should have atleast 6 characters")
            return
        }

        if (mPassword != mCPassword) {
            UtilFunctions.showToast(context, "Password's doesn't match ")
            return
        }

        //TODO
        //Save User data
//        UtilFunctions.showToast(context, "Registering")


        onPasswordCreated.invoke(onlyPhoneNumber,mPassword)
    }


    private fun getPhoneNumber(): String
    {
        val nCountryCode = binding.countryCode.selectedCountryCode
        val mPhoneNumber = binding.etUserName.text.toString()

        Log.e("Test","+$nCountryCode$mPhoneNumber")

        return "$nCountryCode$mPhoneNumber"
    }

    fun showPasswordLayout(isUserExists: Boolean) {
        binding.etUserName.visibility = View.GONE

        if (isUserExists) {
            binding.textView2.text = "Enter your Password"
            binding.etPassword.visibility = View.VISIBLE

            binding.btLogin.text = "Login"
        } else {
            binding.textView2.text = "Create your Password"
            binding.etPassword.visibility = View.VISIBLE

            binding.btLogin.text = "Register"
        }

    }

    fun showPB(status: Boolean)
    {
        if(status)
        {
            binding.progressBar.visibility=View.VISIBLE
            binding.btLogin.visibility=View.GONE
        }else{
            binding.progressBar.visibility=View.INVISIBLE
            binding.btLogin.visibility=View.VISIBLE
        }
    }

}
