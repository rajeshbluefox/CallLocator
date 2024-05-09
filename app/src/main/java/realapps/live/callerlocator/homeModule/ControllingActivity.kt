package realapps.live.callerlocator.homeModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.databinding.ActivityControllingBinding
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils

@AndroidEntryPoint
class ControllingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControllingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControllingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources,binding.mainLayout)

        onClickListeners()
    }

    private fun onClickListeners() {
        binding.btCallLocator.setOnClickListener {
            SelectedButton.buttonClicked=1

            CallIntent.goToHomeActivity(this, true, this)

        }

        binding.btCallTheme.setOnClickListener {
            SelectedButton.buttonClicked=2

            CallIntent.goToHomeActivity(this, true, this)

        }

        binding.btCallAnnouncer.setOnClickListener {
            SelectedButton.buttonClicked=3

            CallIntent.goToHomeActivity(this, true, this)

        }

        binding.ivMobileTools.setOnClickListener {
            CallIntent.goToPhoneToolActivity(this, false, this)

        }

        binding.ivSimInfomatoin.setOnClickListener {
            CallIntent.goToSimInfomationActivity(this,false,this)
        }

        binding.ivBankInfomation.setOnClickListener {
            CallIntent.goToBankInfomationActivity(this,false,this)
        }

        binding.btShareApp.setOnClickListener {
            shareApp("DownLoad Family Locator App")
        }
    }

    private fun shareApp(appLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, appLink)
        startActivity(Intent.createChooser(shareIntent, "Share App"))
    }
}

object SelectedButton{
    var buttonClicked = -1
}