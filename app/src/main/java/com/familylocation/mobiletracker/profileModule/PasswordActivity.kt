package com.familylocation.mobiletracker.profileModule

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.familylocation.mobiletracker.databinding.ActivityPasswordBinding
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions

class PasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setOnClickListeners()
//        setContentView(R.layout.activity_password)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

    private fun setOnClickListeners()
    {
        binding.btBack.setOnClickListener{
            finish()
        }

        binding.btSubmit.setOnClickListener{

        }
    }

    fun checkFields()
    {
        val mOldPassword = binding.etOldPassword.text.toString()
        val mNewPassword = binding.etNewPassword.text.toString()
        val mConfirmPassword = binding.etConfirmPassword.text.toString()

        if(mOldPassword.isEmpty()) {
            UtilFunctions.showToast(this,"Enter Old Password")

        }

        if(mNewPassword.isEmpty()) {
            UtilFunctions.showToast(this,"Enter New Password")

        }

        if(mConfirmPassword.isEmpty()) {
            UtilFunctions.showToast(this,"Enter Confirm Password")
        }
    }
}