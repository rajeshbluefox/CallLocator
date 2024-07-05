package com.familylocation.mobiletracker.homeModule

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.familylocation.mobiletracker.BaseActivity
import com.familylocation.mobiletracker.MainActivity
import com.familylocation.mobiletracker.MyApplication
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.backgroundLocation.utils.PermissionUtils
import com.familylocation.mobiletracker.backgroundLocation.viewmodels.LocationUpdateViewModel
import com.familylocation.mobiletracker.bankInformationModule.BankInfomationActivity
import com.familylocation.mobiletracker.callLocatorModule.apiFunctions.CallLocatorViewModel
import com.familylocation.mobiletracker.databinding.ActivityControllingBinding
import com.familylocation.mobiletracker.loginModule.LoginActivity
import com.familylocation.mobiletracker.nearByPlacesModule.CompassActivity
import com.familylocation.mobiletracker.nearByPlacesModule.LocationActivity
import com.familylocation.mobiletracker.phoneToolsModule.ISDCodeActivity
import com.familylocation.mobiletracker.phoneToolsModule.PhoneToolActivity
import com.familylocation.mobiletracker.simInfoModule.SimInfomationActivity
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.LoginData
import com.google.android.gms.location.LocationSettingsStates
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.library.LiveSharedPreferences


@AndroidEntryPoint
class ControllingActivity : BaseActivity() {

    private lateinit var binding: ActivityControllingBinding

    private lateinit var firebaseAnalytics : FirebaseAnalytics

    private lateinit var callLocatorViewModel: CallLocatorViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControllingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callLocatorViewModel = ViewModelProvider(this)[CallLocatorViewModel::class.java]

        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources, binding.contentLt)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


//        MyApplication.getInstance().getmAdsId().add(getString(R.string.admob_native_Ad_test))
//        MyApplication.getInstance().loadNativeOptional(0)
        //Load interstitial Ad
        MyApplication.getInstance().loadInterstitialAd()

//        loadBannerAds(binding.relBanner)
        MyApplication.getInstance().loadNativeAd(binding.rlAdplaceholder1, this@ControllingActivity)
        MyApplication.getInstance().loadNativeAd(binding.rlAdplaceholder, this@ControllingActivity)

        initLocationVariables()
        onClickListeners()
    }




    private fun onClickListeners() {

        binding.btCallLocator.setOnClickListener {
            SelectedButton.buttonClicked = 1

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
            firebaseAnalytics.logEvent("FamilyLocatorButtonClicked", bundle)

            checkButtonClickandGo()
        }

        binding.btCallThemes.setOnClickListener {
            SelectedButton.buttonClicked = 2

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
            firebaseAnalytics.logEvent("CallerThemesButtonClicked", bundle)

            val intent = Intent(
                this@ControllingActivity,
                HomeActivity::class.java
            )

            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, true,layoutInflater,this)

        }

        binding.btCallAnnouncer.setOnClickListener {
            SelectedButton.buttonClicked = 3

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
            firebaseAnalytics.logEvent("CallerAnnouncerButtonClicked", bundle)

            val intent = Intent(
                this@ControllingActivity,
                HomeActivity::class.java
            )

            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, true,layoutInflater,this)

        }

        binding.btMobileTools.setOnClickListener {
//            CallIntent.goToPhoneToolActivity(this, false, this)

            val intent = Intent(
                this@ControllingActivity,
                PhoneToolActivity::class.java
            )

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
//            UtilFunctions.logEvent(this@ControllingActivity, "MobileTools Button Clicked", bundle)
            firebaseAnalytics.logEvent("MobileToolsButtonClicked", bundle)


            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, false,layoutInflater,this)


        }

        binding.btCurrentLocator.setOnClickListener {
            val intent = Intent(
                this@ControllingActivity,
                LocationActivity::class.java
            )

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
//            UtilFunctions.logEvent(this@ControllingActivity, "CurrentLocation Button Clicked", bundle)
            firebaseAnalytics.logEvent("CurrentLocationButtonClicked", bundle)


            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, false,layoutInflater,this)

        }

        binding.btSimCard.setOnClickListener {
//            CallIntent.goToSimInfomationActivity(this,false,this)

            val intent = Intent(
                this@ControllingActivity,
                SimInfomationActivity::class.java
            )

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
//            UtilFunctions.logEvent(this@ControllingActivity, "SimCard Button Clicked", bundle)
            firebaseAnalytics.logEvent("SimCardButtonClicked", bundle)


            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, false,layoutInflater,this)

        }

        binding.btBankInformation.setOnClickListener {
//            CallIntent.goToBankInfomationActivity(this,false,this)

            val intent = Intent(
                this@ControllingActivity,
                BankInfomationActivity::class.java
            )

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
//            UtilFunctions.logEvent(this@ControllingActivity, "BankInfo Button Clicked", bundle)
            firebaseAnalytics.logEvent("BankInfoButtonClicked", bundle)


            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, false,layoutInflater,this)

        }

        binding.btCompass.setOnClickListener {
//            CallIntent.goToCompassActivity(this,false,this)
            val intent = Intent(
                this@ControllingActivity,
                CompassActivity::class.java
            )

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
//            UtilFunctions.logEvent(this@ControllingActivity, "Compass Button Clicked", bundle)
            firebaseAnalytics.logEvent("CompassButtonClicked", bundle)


            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, false,layoutInflater,this)

        }

        binding.btISDCodes.setOnClickListener {
            val intent = Intent(
                this@ControllingActivity,
                ISDCodeActivity::class.java
            )

            val bundle = Bundle()
            bundle.putString("UserMobileNo",LoginData.getUserPhone(this@ControllingActivity))
            firebaseAnalytics.logEvent("ISDCodesButtonClicked", bundle)


            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, false,layoutInflater,this)

        }

        binding.btShareApp.setOnClickListener {
            shareApp("DownLoad Family Locator App")
        }
    }

    private fun checkButtonClickandGo()
    {
        if(LoginData.getUserLoginStatus(this))
        {

            val intent = Intent(
                this@ControllingActivity,
                HomeActivity::class.java
            )

            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, true,layoutInflater,this)

        }else{

            val intent = Intent(
                this@ControllingActivity,
                LoginActivity::class.java
            )

            MyApplication.getApplication()
                .displayInterstitialAds(this@ControllingActivity, intent, false,layoutInflater,this)

//            CallIntent.goToLoginActivity(this,true,this)
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        moveTaskToBack(true)

    }

    private fun shareApp(appLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, appLink)
        startActivity(Intent.createChooser(shareIntent, "Share App"))
    }


    ///Background Location Permission



    override fun onStart() {
        super.onStart()

        if(LoginData.getUserLoginStatus(this))
        {
            checkLocationPermission()
        }
    }

    private fun initLocationVariables()
    {
        val preferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
        liveSharedPreferences = LiveSharedPreferences(preferences)
    }

    private lateinit var liveSharedPreferences: LiveSharedPreferences

    private val locationUpdateViewModel by lazy {
        ViewModelProvider(this).get(LocationUpdateViewModel::class.java)
    }

    private val resolutionForResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
        val states = activityResult.data?.let { LocationSettingsStates.fromIntent(it) }
        when (activityResult.resultCode) {
            RESULT_OK ->
                if (states?.isLocationUsable == true) {
                    with(liveSharedPreferences.preferences.edit()) {
                        putBoolean("location_services_enabled", true)
                        apply()
                    }
                    locationUpdateViewModel.startLocationUpdates()
                    Log.i(TAG, "Location services have been enabled")
                }
            RESULT_CANCELED ->
                showLocationServicesWarning()
        }
    }


    private fun checkLocationPermission() {
        when {
            !PermissionUtils.isForegroundLocationPermissionGranted(this) -> {
                PermissionUtils.requestPermission(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            !PermissionUtils.isBackgroundLocationPermissionGranted(this) -> {
                if (PermissionUtils.shouldShowRationale(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    || locationUpdateViewModel.hasRequestedBackgroundLocation
                ) {
                    showBackgroundLocationRationale()
                } else {
                    // This will never get reached as a rationale should always be shown for this permission
                    requestBackgroundLocationPermission()
                }
            }
            else -> {
                locationUpdateViewModel.startLocationUpdates()
                locationPermissionGranted()
            }
        }
    }

    private fun requestBackgroundLocationPermission() {
        // Open the location permissions for the app where the user can select "Allow all the time".
        // If we have asked previously but the user has denied it, that request will fail. So instead
        // open the app settings where the user can navigate manually to the location permissions
        // and modify it.
        dialog?.dismiss()

        if (!locationUpdateViewModel.hasRequestedBackgroundLocation) {
            UtilFunctions.isInPermissionFlow=true
            PermissionUtils.requestPermission(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            locationUpdateViewModel.hasRequestedBackgroundLocation = true
        } else {
            PermissionUtils.openAppSettings(this)
        }
    }

    /**
     * Result from requesting runtime permissions can be checked here.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        UtilFunctions.isInPermissionFlow=false

        if (requestCode != PermissionUtils.PERMISSION_REQUEST_CODE) {
            // We received some permission result from someone/something else.
            return
        }

        if (grantResults.isEmpty()) {
            // The permission request was cancelled
            return
        }

        if (permissions.any {
                it.contentEquals(Manifest.permission.ACCESS_FINE_LOCATION)
                        || it.contentEquals(Manifest.permission.ACCESS_COARSE_LOCATION)
            } && grantResults.all { it == PackageManager.PERMISSION_DENIED }) {
            locationPermissionsDenied()
        } else if (permissions.any { it.contentEquals(Manifest.permission.ACCESS_BACKGROUND_LOCATION) }
            && grantResults.all { it == PackageManager.PERMISSION_DENIED }) {
            showBackgroundLocationRationale()
        } else {
            // Check if any other permissions need to be requested
            checkLocationPermission()
        }
    }

    private var dialog: AlertDialog? = null

    /**
     * Ask the user to allow location updates "all the time" so that we can receive location updates
     * while the app is closed.
     */
    private fun showBackgroundLocationRationale() {
        if (dialog == null) {
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle(R.string.location_rationale_title)
                .setMessage(R.string.location_rationale)
                .setPositiveButton(getString(R.string.got_it)) { _, _ -> requestBackgroundLocationPermission() }
                .setNegativeButton(getString(R.string.no_thanks)) { _, _ ->
                    locationPermissionsDenied()
                }
                .create()
        }

        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    private var locationServicesWarning: Snackbar? = null

    /**
     * Ask the user to turn on Location Services so that we can receive location updates.
     */
    private fun showLocationServicesWarning() {
        if (locationServicesWarning == null) {
            locationServicesWarning = Snackbar.make(
                binding.root,
                R.string.enable_location_services_text,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok) {
                    locationUpdateViewModel.requestLocationServices(this, resolutionForResult)
                    dismissWarning()
                }
        }

        if (locationServicesWarning?.isShown == false) {
            locationServicesWarning?.show()
        }
    }

    /**
     * Allows us to explicitly dismiss this warning.
     */
    private fun dismissWarning() {
        locationServicesWarning?.dismiss()
    }

    /**
     * Ask the user to allow Location permissions so that we can receive location updates.
     */
    private fun showLocationPermissionsWarning() {
        Snackbar.make(
            binding.root,
            R.string.error_permissions_denied,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.settings) {
                PermissionUtils.openAppSettings(this)
            }
            .show()
    }

    private fun locationPermissionsDenied() {
        Log.e("Test","Location Permission Denied")

//        binding.mapImage.setImageDrawable(
//            AppCompatResources.getDrawable(
//                this,
//                R.drawable.ic_access_denied
//            )
//        )
        showLocationPermissionsWarning()
    }

    private fun locationPermissionGranted() {
        Log.e("Test","Location Permission Granted")
//        binding.mapImage.setImageDrawable(
//            AppCompatResources.getDrawable(
//                this,
//                R.drawable.ic_map_dark
//            )
//        )

        if(!isLocationEnabled())
        {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {

        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    companion object {
        private val TAG = MainActivity::class.simpleName
    }




}

object SelectedButton {
    var buttonClicked = -1
}