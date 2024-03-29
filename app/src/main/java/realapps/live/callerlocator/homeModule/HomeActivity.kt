package realapps.live.callerlocator.homeModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callLocatorModule.CallLocatorFragment
import realapps.live.callerlocator.callSettingsModule.CallSettingsFragment
import realapps.live.callerlocator.callThemesModule.CallThemesFragment
import realapps.live.callerlocator.databinding.ActivityHomeBinding
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
import realapps.live.callerlocator.zSharedPreference.LoginData

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
//        homeActivityViewModel = ViewModelProvider(this)[HomeActivityViewModel::class.java]
        setContentView(binding.root)
        StatusBarUtils.transparentStatusBar(this)

//        LoginData.saveUserPhone(this, "9123456789")


        setIcon(1)
        onClickListeners()
        gotoCallLocatorFragment()
    }

    private fun onClickListeners() {
        binding.btCallLocator.setOnClickListener {
            gotoCallLocatorFragment()

            setIcon(1)
        }

        binding.btCallThemes.setOnClickListener {
            gotoCallSettingsFragment()

            setIcon(2)
        }

        binding.btSettings.setOnClickListener {
            gotoCallThemesFragment()

            setIcon(3)
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, CallLocatorFragment())
            .commit()
    }

    private fun gotoCallSettingsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, CallSettingsFragment())
            .commit()
    }

    private fun gotoCallThemesFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, CallThemesFragment())
            .commit()
    }
}