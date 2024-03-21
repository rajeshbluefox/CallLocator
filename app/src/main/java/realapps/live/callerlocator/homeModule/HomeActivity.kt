package realapps.live.callerlocator.homeModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callLocatorModule.CallLocatorFragment
import realapps.live.callerlocator.callSettingsModule.CallSettingsFragment
import realapps.live.callerlocator.callThemesModule.CallThemesFragment
import realapps.live.callerlocator.databinding.ActivityHomeBinding
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
//        homeActivityViewModel = ViewModelProvider(this)[HomeActivityViewModel::class.java]
        setContentView(binding.root)
        StatusBarUtils.transparentStatusBar(this)


        onClickListeners()
        gotoCallLocatorFragment()
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