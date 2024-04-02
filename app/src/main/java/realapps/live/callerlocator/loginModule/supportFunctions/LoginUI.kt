package realapps.live.callerlocator.loginModule.supportFunctions

import android.content.Context
import android.util.Log
import android.view.View
import realapps.live.callerlocator.databinding.ActivityLoginBinding
import realapps.live.callerlocator.loginModule.modalClass.UserInfoDataClass
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.LoginData


class LoginUI(
    val context: Context,
    private val binding: ActivityLoginBinding,
    private val onNumberValidated: (phoneNumber: String) -> Unit,
    private val onPasswordValidated: () -> Unit,
    private val onPasswordCreated: (phoneNumber: String,password: String) -> Unit
) {

    fun checkFields() {
        val mUserName = binding.etUserName.text.toString()
//        val mPassword = binding.etPassword.text.toString()

        if (mUserName.isEmpty()) {
            UtilFunctions.showToast(context, "Enter MobileNumber")
            return
        }

        if (mUserName.length != 10) {
            UtilFunctions.showToast(context, "Enter Valid MobileNumber")
            return
        }

        onNumberValidated.invoke(mUserName)
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
        val mPhoneNumber = binding.etUserName.text.toString()
        val mPassword = binding.etPassword.text.toString()
        val mCPassword = binding.etCPassword.text.toString()

        Log.e("Test","Passwords - $mPassword = $mCPassword")

        if (mPhoneNumber.isEmpty()) {
            UtilFunctions.showToast(context, "Enter MobileNumber")
            return
        }

        if (mPhoneNumber.length != 10) {
            UtilFunctions.showToast(context, "Enter Valid MobileNumber")
            return
        }

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

        onPasswordCreated.invoke(mPhoneNumber,mPassword)
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
