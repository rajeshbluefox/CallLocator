package realapps.live.callerlocator.profileModule

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import realapps.live.callerlocator.R
import realapps.live.callerlocator.databinding.ActivityPasswordBinding
import realapps.live.callerlocator.databinding.ActivityProfileBinding
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions

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