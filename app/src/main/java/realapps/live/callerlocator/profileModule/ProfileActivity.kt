package realapps.live.callerlocator.profileModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.databinding.ActivityProfileBinding
import realapps.live.callerlocator.loginModule.LogoutDialog
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zSharedPreference.LoginData

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var logoutDialog: LogoutDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        onClickListeners()
    }

    private fun initViews() {
        logoutDialog = LogoutDialog(layoutInflater,this,::onLogoutClicked)
    }

    private fun onLogoutClicked()
    {
        LoginData.saveUserLoginStatus(this,false)
        CallIntent.goToLoginActivity(this,true,this)
    }
    private fun onClickListeners() {
        binding.btBack.setOnClickListener {
            finish()
        }

        binding.btLogout.setOnClickListener {
            logoutDialog.openLogoutDialog()
        }
    }

}