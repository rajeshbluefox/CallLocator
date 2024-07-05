package com.familylocation.mobiletracker

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.familylocation.mobiletracker.databinding.ActivityMainBinding
import com.familylocation.mobiletracker.zCommonFuntions.CallIntent
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.LoginData
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val ONESIGNAL_APP_ID = "14a3e5b0-ae08-48d8-aab2-a03e150b0466"

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.transparentStatusBar(this)

        initFirebaseAnalytics()

        MyApplication.getInstance().getmAdsId().add(getString(R.string.admob_native_Ad_test))
        MyApplication.getInstance().loadNativeOptional(0)

        MyApplication.getInstance().loadInterstitialAd()
//        MyApplication.getInstance().getmAdsId().add(getString(R.string.admob_native_Ad_test))
//        val appLifecycleObserver = OpenAppManager(MyApplication.getInstance())
//        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPostNotificationsPermission()
        } else {
            checkLoginStatusandNavigate()
        }
    }

    fun initFirebaseAnalytics() {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
    }

    private fun initOneSignal() {
        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
    }

    private fun checkLoginStatusandNavigate() {

//        initOneSignal()
        loadAd(this)

    }

    private fun checkLoginStatusandGo() {
        if (LoginData.getUserLoginStatus(this)) {

            val bundle = Bundle()
            bundle.putString("UserMobileNo", LoginData.getUserPhone(this@MainActivity))
            UtilFunctions.logEvent(this@MainActivity, "SplashScreen", bundle)

            CallIntent.goToControllingActivity(this, true, this)

        } else {

            val bundle = Bundle()
            bundle.putString("UserMobileNo", "User not loggedIn")
            UtilFunctions.logEvent(this@MainActivity, "SplashScreen", bundle)

//            CallIntent.goToControllingActivity(this, true, this)


            if(!LoginData.getTutorialStatus(this))
            {
                showTutorials()
            }else{
                CallIntent.goToControllingActivity(this, true, this)
            }


        }
    }

    private val POST_NOTIFICATIONS_REQUEST_CODE = 100


    private fun requestPostNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check if the app has the permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    POST_NOTIFICATIONS_REQUEST_CODE
                )
            } else {
                checkLoginStatusandNavigate()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == POST_NOTIFICATIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // You can proceed with showing notifications
                checkLoginStatusandNavigate()
            } else {
                checkLoginStatusandNavigate()
//                UtilFunctions.showToast(this,"Allow Notifications to proceed")
                // Permission denied
                // Handle the case where the user denies the permission
            }
        }
    }

    private val AD_UNIT_ID = "ca-app-pub-4644402642324922/3240950747"

//    private final String AD_UNIT_ID = getString(R.string.admob_open_Ad_test);

    private var appOpenAd: AppOpenAd? = null
    private fun loadAd(context: Context) {
        // We will implement this below.
        // Do not load ad if there is an unused ad or one is already loading.

        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context, AD_UNIT_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    // Called when an app open ad has loaded.
                    Log.e("Test", "Ad was loaded.")

                    appOpenAd = ad

                    showOpenAd()

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Called when an app open ad has failed to load.
                    Log.e("Test", loadAdError.message)

                    checkLoginStatusandGo()

                }
            })
    }

    fun showOpenAd() {
        appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                // Set the reference to null so isAdAvailable() returns false.
                Log.d("AppOpenAdManager.LOG_TAG", "Ad dismissed fullscreen content.")
                appOpenAd = null
                checkLoginStatusandGo()


            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                // Set the reference to null so isAdAvailable() returns false.
                Log.d("AppOpenAdManager.LOG_TAG", adError.message)
                appOpenAd = null
                checkLoginStatusandGo()


            }

            override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                Log.d("AppOpenAdManager.LOG_TAG", "Ad showed fullscreen content.")
            }
        }
        appOpenAd!!.show(this)
    }

    private val images = listOf(
        R.drawable.tut1,
        R.drawable.tut2,
        R.drawable.tut3,
        R.drawable.tut4
    )
    private var currentIndex = 0

    private lateinit var dots: Array<ImageView>


    private fun showTutorials() {
        binding.splashLayout.visibility = View.GONE

        binding.tutorialsLayout.visibility = View.VISIBLE

        StatusBarUtils.setTopPadding(resources,binding.tutorialsLayout)

        MyApplication.getInstance().loadNativeAd(binding.rlAdplaceholder, this@MainActivity)

        dots = arrayOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4
        )


        updateImageView(animateForward = true)
        onClickListeners()


    }

    private fun updateDots(position: Int) {
        dots.forEachIndexed { index, imageView ->
            if (index == position) {
                imageView.setImageResource(R.drawable.dot_indicator_sel)
            } else {
                imageView.setImageResource(R.drawable.dot_indicator_unsel)
            }
        }
    }

    fun onClickListeners() {
        binding.btPrevious.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateImageView(animateForward = false)
            }
        }

        binding.btNext.setOnClickListener {
            if (currentIndex < images.size - 1) {
                currentIndex++
                updateImageView(animateForward = true)
            }else{

//                val intent = Intent(
//                    this@MainActivity,
//                    ControllingActivity::class.java
//                )
//
//                MyApplication.getApplication()
//                    .displayInterstitialAds(this@MainActivity, intent, false,layoutInflater,this)

                LoginData.saveTutorialStatus(this,true)
                CallIntent.goToControllingActivity(this,true,this)
            }
        }
    }

    private fun updateImageView(animateForward: Boolean) {
        val context = this

        val animOut = AnimationUtils.loadAnimation(
            context,
            if (animateForward) R.anim.slide_out_left else R.anim.slide_out_right
        )
        val animIn = AnimationUtils.loadAnimation(
            context,
            if (animateForward) R.anim.slide_in_right else R.anim.slide_in_left
        )

        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                binding.tutorialsIv.setImageResource(images[currentIndex])
                binding.tutorialsIv.startAnimation(animIn)
            }
        })

        binding.tutorialsIv.startAnimation(animOut)

        binding.btPrevious.visibility = if (currentIndex == 0) View.INVISIBLE else View.VISIBLE
        binding.btNext.visibility = if (currentIndex == images.size - 1) {
            View.VISIBLE }else {View.VISIBLE}

        if(currentIndex == images.size - 1)
        {
            binding.btNext.setImageResource(R.drawable.tut_start)
        }

        updateDots(currentIndex)
    }


}