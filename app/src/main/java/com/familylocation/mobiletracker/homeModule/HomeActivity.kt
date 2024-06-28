package com.familylocation.mobiletracker.homeModule

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import com.familylocation.mobiletracker.BaseActivity
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callLocatorModule.CallLocatorFragment
import com.familylocation.mobiletracker.callSettingsModule.CallSettingsFragment
import com.familylocation.mobiletracker.callThemesModule.CallThemesFragment
import com.familylocation.mobiletracker.databinding.ActivityHomeBinding
import com.familylocation.mobiletracker.zCommonFuntions.CallIntent
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtils.transparentStatusBar(this)

//        LoginData.saveUserPhone(this, "9123456789")

       loadBannerAds(binding.relBanner)


        setFragment()
        onClickListeners()

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        CallIntent.goToControllingActivity(this,true,this)

    }

    private fun setFragment()
    {

        when(SelectedButton.buttonClicked)
        {
            1 -> {
                gotoCallLocatorFragment()
            }
            2 ->{
                gotoCallThemesFragment()
            }
            3 ->{
                gotoCallSettingsFragment()
            }
        }
    }

    private fun onClickListeners() {
        binding.btCallLocator.setOnClickListener {
            gotoCallLocatorFragment()

        }

        binding.btCallThemes.setOnClickListener {
            gotoCallSettingsFragment()

        }

        binding.btSettings.setOnClickListener {
            gotoCallThemesFragment()

        }
    }

    fun setIcon(selectedItem: Int) {
        when (selectedItem) {
            1 -> {
                binding.btCallLocator.setImageResource(R.drawable.caller_locator_s)
                binding.btCallThemes.setImageResource(R.drawable.call_settings_us)
                binding.btSettings . setImageResource (R.drawable.call_theme_us)

                binding.tvCallLocator.setTextColor(resources.getColor(R.color.white))
                binding.tvCallThemes.setTextColor(resources.getColor(R.color.text_color_1))
                binding.tvSettings.setTextColor(resources.getColor(R.color.text_color_1))

                binding.ivCallLocatorSelected.visibility=View.VISIBLE
                binding.ivCallSettingsSelected.visibility=View.INVISIBLE
                binding.ivCallThemesSelected.visibility=View.INVISIBLE

            }
            2 -> {
                binding.btCallLocator.setImageResource(R.drawable.caller_locator_us)
                binding.btCallThemes.setImageResource(R.drawable.call_settings_s)
                binding.btSettings.setImageResource(R.drawable.call_theme_us)

                binding.tvCallLocator.setTextColor(resources.getColor(R.color.text_color_1))
                binding.tvCallThemes.setTextColor(resources.getColor(R.color.white))
                binding.tvSettings.setTextColor(resources.getColor(R.color.text_color_1))

                binding.ivCallLocatorSelected.visibility=View.INVISIBLE
                binding.ivCallSettingsSelected.visibility=View.INVISIBLE
                binding.ivCallThemesSelected.visibility=View.VISIBLE

            }
            3 -> {
                binding.btCallLocator.setImageResource(R.drawable.caller_locator_us)
                binding.btCallThemes.setImageResource(R.drawable.call_settings_us)
                binding.btSettings.setImageResource(R.drawable.call_theme_s)

                binding.tvCallLocator.setTextColor(resources.getColor(R.color.text_color_1))
                binding.tvCallThemes.setTextColor(resources.getColor(R.color.text_color_1))
                binding.tvSettings.setTextColor(resources.getColor(R.color.white))

                binding.ivCallLocatorSelected.visibility=View.INVISIBLE
                binding.ivCallSettingsSelected.visibility=View.VISIBLE
                binding.ivCallThemesSelected.visibility=View.INVISIBLE

            }
        }
    }

    private fun gotoCallLocatorFragment() {
        setIcon(1)
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, CallLocatorFragment())
            .commit()
    }

    private fun gotoCallSettingsFragment() {
        setIcon(2)
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, CallSettingsFragment())
            .commit()
    }

    private fun gotoCallThemesFragment() {
        setIcon(3)
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, CallThemesFragment())
            .commit()
    }
}