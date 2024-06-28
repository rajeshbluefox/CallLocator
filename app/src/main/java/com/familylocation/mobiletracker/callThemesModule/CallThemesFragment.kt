package com.familylocation.mobiletracker.callThemesModule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.familylocation.mobiletracker.MyApplication
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callThemesModule.apiFunctions.CallThemesApiFunctions
import com.familylocation.mobiletracker.callThemesModule.apiFunctions.CallThemesViewModel
import com.familylocation.mobiletracker.callThemesModule.callAlertWindow.BackgroundCallService
import com.familylocation.mobiletracker.callThemesModule.modalClass.ThemesData
import com.familylocation.mobiletracker.callThemesModule.supportFunctions.CallerThemesAdapterNew
import com.familylocation.mobiletracker.callThemesModule.supportFunctions.CallerThemesUI
import com.familylocation.mobiletracker.callThemesModule.supportFunctions.SpacesItemDecoration
import com.familylocation.mobiletracker.databinding.FragmentCallThemesBinding
import com.familylocation.mobiletracker.setThemeModule.SetThemeActivity
import com.familylocation.mobiletracker.zCommonFuntions.CallIntent
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.SettingsData
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class CallThemesFragment : Fragment() {

    private lateinit var binding: FragmentCallThemesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_themes, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViews()
        getThemes()
        onClickListeners()
    }

    private lateinit var callerThemesUI: CallerThemesUI

    private lateinit var callThemesApiFunctions: CallThemesApiFunctions

    private lateinit var callThemesViewModel: CallThemesViewModel

    private fun initViews() {
        callThemesViewModel = ViewModelProvider(this)[CallThemesViewModel::class.java]
        callThemesApiFunctions =
            CallThemesApiFunctions(
                requireContext(),
                requireActivity(),
                viewLifecycleOwner,
                callThemesViewModel,
                ::getThemesResponse
            )
        callerThemesUI = CallerThemesUI(requireContext(), binding)
//        callerThemesUI.setTopPadding(resources)
        StatusBarUtils.setTopPadding(resources, binding.callThemeLt)
        val callThemeStatus = SettingsData.getCallThemeStatus(requireContext())

        binding.btSwitchCallTheme.isChecked = callThemeStatus
        if (callThemeStatus)
            startBackgroundService()

    }

    private fun getThemesResponse(data: List<ThemesData>) {
         themes = ArrayList(data.reversed())
        initThemes()
    }

    private fun onClickListeners() {

        binding.btSwitchCallTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAndRequestPermissions()
                SettingsData.saveCallThemeStatus(requireContext(), true)
            } else {
                stopBackgroundService()
                SettingsData.saveCallThemeStatus(requireContext(), false)
            }
        }

        binding.btHome.setOnClickListener {
            CallIntent.goToControllingActivity(requireContext(),true,requireActivity())
        }
    }


    private fun getThemes() {
        callThemesApiFunctions.getThemes()
    }

    private var themes = ArrayList<ThemesData>()

    private fun initThemes() {

        addDummyItems(themes)

        var nativeAd: NativeAd? = null
        val mNativeAdsGHome = MyApplication.getApplication().gNativeHome
        if (mNativeAdsGHome.size > 0) {
            nativeAd = mNativeAdsGHome[0]
        }


        val callerThemesAdapter = CallerThemesAdapterNew(
            requireContext(),
            themes,
            ::onThemeSelected,
            nativeAd
        )

//        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)

//        binding.rvThemes.apply {
//            layoutManager = GridLayoutManager(
//                context,
//                3,  // Number of items in each row
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//            addItemDecoration(SpacesItemDecoration(spacingInPixels, 3))
//            adapter = callerThemesAdapter
//        }

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)

        val layoutManager = GridLayoutManager(context, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (callerThemesAdapter.getItemViewType(position)) {
                    CallerThemesAdapterNew.VIEW_TYPE_AD -> layoutManager.spanCount
                    CallerThemesAdapterNew.VIEW_TYPE_THEME -> 1
                    else -> 1
                }
            }
        }

        binding.rvThemes.apply {
            this.layoutManager = layoutManager
            addItemDecoration(SpacesItemDecoration(spacingInPixels, 3))
            adapter = callerThemesAdapter
        }

    }

    private fun addDummyItems(themes: ArrayList<ThemesData>) {
        val dummyThemes = ThemesData()
        dummyThemes.id = "0"

        // New list to store the result with dummy items
        val newThemes = ArrayList<ThemesData>()

        for (i in themes.indices) {
            newThemes.add(themes[i])
            // Check if we need to add a dummy item
            if ((i + 1) % 6 == 0) {
                newThemes.add(dummyThemes)
            }
        }

        // Update the original list
        themes.clear()
        themes.addAll(newThemes)
    }


    private fun onThemeSelected() {

        val intent = Intent(
            requireContext(),
            SetThemeActivity::class.java
        )
        MyApplication.getApplication()
            .displayInterstitialAds(requireActivity(), intent, false,layoutInflater,requireContext())

//        CallIntent.goToSetThemeActivity(requireContext(), false, requireActivity())
    }

    ///////////////////////Call Theme Permissions///////////////////////

    private val CALL_PERMISSION_REQUEST_CODE = 123
    private val ANSWER_PHONE_CALLS_REQUEST_CODE = 124
    private val REQUEST_OVERLAY_PERMISSION = 125
    private val REQUEST_CALL_LOG_PERMISSION = 126
    private val REQUEST_OVERLAY_RUNNING_PERMISSION = 127
    private val FOREGROUND_SERVICE_CONNECTED_DEVICE_PERMISSION_REQUEST_CODE = 100


    private val phoneStatePermissions = arrayOf(android.Manifest.permission.READ_PHONE_STATE)
    private val overlayPermissions = arrayOf(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    private val callLogPermissions = arrayOf(Manifest.permission.READ_CALL_LOG)
    private val answerPhoneCallsPermissions = arrayOf(Manifest.permission.ANSWER_PHONE_CALLS)

//    private val overlayPermissions = arrayOf(
//        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//        Manifest.permission.SYSTEM_ALERT_WINDOW
//    )

    private fun checkAndRequestPermissions() {
//        checkAndRequestCallLogPermission()
//        checkAndRequestAnswerPhoneCallsPermission()
        checkAndRequestOverlayPermission()
//        checkAndRequestPhoneStatePermission()
    }

    private fun checkAndRequestPhoneStatePermission() {
        if (!arePermissionsGranted(phoneStatePermissions)) {
            requestPermissions(phoneStatePermissions, CALL_PERMISSION_REQUEST_CODE)
        } else {
            Log.e("Test", "Phone State Permission Already Granted 4")

            // Permission already granted, proceed with your logic

//            checkForeGroundServicePermission()
            startBackgroundService()
        }
    }

    private fun checkAndRequestOverlayPermission() {
        Log.e("Test", "72")
//        !Settings.canDrawOverlays(requireContext())

        Log.e("Test", "OverLays ${Settings.canDrawOverlays(requireContext())}")
        if (!Settings.canDrawOverlays(requireContext())) {
            Log.e("Test", "74")
            requestOverlayPermission()
//            requestPermissions(overlayPermissions, REQUEST_OVERLAY_PERMISSION)
        } else {
            Log.e("Test", "RequestOverlay Permission Already Granted 3")

//            checkAndRequestOverlayPermissionRunning()
            checkAndRequestPhoneStatePermission()

            // Permission already granted or not needed
//            openYourActivity()
        }
    }

    fun checkAndRequestOverlayPermissionRunning()
    {
        Log.e("Test", "RequestOverlay Running Permission ")

        if ("xiaomi" == Build.MANUFACTURER.toLowerCase(Locale.ROOT)) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            intent.putExtra("extra_pkgname", "package:realapps.live.callerlocator")
            startActivityForResult(intent, REQUEST_OVERLAY_RUNNING_PERMISSION)
        }


    }

    private fun requestOverlayPermission() {
        Log.e("Test", "ROP")
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:com.familylocation.mobiletracker")
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
    }

    private fun checkAndRequestAnswerPhoneCallsPermission() {
        Log.e("Test", "AnswerPhoneCalls Premission Check 2")

        if (!arePermissionsGranted(answerPhoneCallsPermissions)) {
            Log.e("Test", "AnswerPhoneCalls Premission Request 2")

            requestPermission(answerPhoneCallsPermissions, ANSWER_PHONE_CALLS_REQUEST_CODE)
        } else {
            Log.e("Test", "AnswerPhoneCalls Permission Already Granted 2")

//            checkAndRequestOverlayPermission()
            // Permission already granted, proceed with your logic
        }
    }

    private fun checkAndRequestCallLogPermission() {
        if (!arePermissionsGranted(callLogPermissions)) {
            requestPermissions(callLogPermissions, REQUEST_CALL_LOG_PERMISSION)
        } else {
            Log.e("Test", "CallLogPermission Already Granted 1")

            checkAndRequestOverlayPermission()
//            checkAndRequestAnswerPhoneCallsPermission()
//            UtilFunctions.showToast(this, "Call Logs Permitted")
        }
    }


    fun checkForeGroundServicePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                FOREGROUND_SERVICE_CONNECTED_DEVICE_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.e("Test", "ForeGroundServicePermission Already Granted 5")

            startBGDirectly()
            // Permission is already granted, proceed with starting the foreground service
//            startBackgroundService()
        }
    }


    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            requestCode
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PERMISSION_REQUEST_CODE -> handlePhoneStatePermissionResult(grantResults)
            ANSWER_PHONE_CALLS_REQUEST_CODE -> handleAnswerPhoneCallsPermissionResult(grantResults)
            REQUEST_CALL_LOG_PERMISSION -> handleCallLogPermissionResult(grantResults)
            FOREGROUND_SERVICE_CONNECTED_DEVICE_PERMISSION_REQUEST_CODE -> handleForeGroundServicePermissionResult(
                grantResults
            )
            REQUEST_OVERLAY_RUNNING_PERMISSION -> handleOverlayRunningPermissionResult(grantResults)
        }
    }

//    REQUEST_OVERLAY_PERMISSION -> handleOverlayPermissionResult(grantResults)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_OVERLAY_PERMISSION -> handleOverlayPermissionResult()
            // Add other requestCode cases if needed
        }
    }

    private fun handlePhoneStatePermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with your logic
//            startBackgroundService()
            Log.e("Test", "Phone State Permission Granted 4")
            startBackgroundService()
//            checkForeGroundServicePermission()
        } else {
            // Permission denied. Inform the user.
            Toast.makeText(
                requireContext(),
                "Permission denied. Not able to get Phone State.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleForeGroundServicePermissionResult(grantResults: IntArray) {
        Log.e("Test", "Push Notifications Result")

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with your logic
            Log.e("Test", "ForeGroundServicePermission Granted 5")

            startBGDirectly()
//            startBackgroundService()
        } else {
            // Permission denied. Inform the user.
            Toast.makeText(
                requireContext(),
                "Permission denied. ForeGroundPermission.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleAnswerPhoneCallsPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with your logic
            Log.e("Test", "AnswerPhoneCalls Permission Granted 2")

//            checkAndRequestOverlayPermission()
        } else {
            // Permission denied. Inform the user or handle accordingly.
        }
    }

    private fun handleOverlayPermissionResult() {
        if (Settings.canDrawOverlays(requireContext())) {
            // Permission granted, proceed with your logic
            Log.e("Test", "RequestOverlay Permission Granted 3")

//            checkAndRequestOverlayPermissionRunning()
            checkAndRequestPhoneStatePermission()
//            openYourActivity()
        } else {
            // Permission not granted, show a message or take appropriate action
            Toast.makeText(
                requireContext(),
                "Overlay permission not granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleOverlayRunningPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with your logic
            Log.e("Test", "RequestOverlay Running Permission Granted 3")

            checkAndRequestPhoneStatePermission()
//            openYourActivity()
        } else {
            // Permission not granted, show a message or take appropriate action
            Toast.makeText(
                requireContext(),
                "Overlay permission not granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleCallLogPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("Test", "CallLogPermission Granted 1")
            checkAndRequestOverlayPermission()
//            checkAndRequestAnswerPhoneCallsPermission()
//            UtilFunctions.showToast(this, "Call Logs Permitted")
        } else {
            // Permission denied, explain to user and handle accordingly
            Toast.makeText(requireContext(), "Call log permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun startBackgroundService() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            checkForeGroundServicePermission()
//        }else {
        // Implement your logic for starting the background service
//            UtilFunctions.showToast(requireContext(), "Call Theme Enabled")

        val serviceIntent = Intent(requireContext(), BackgroundCallService::class.java)
        requireContext().startService(serviceIntent)

        checkAndRequestAnswerPhoneCallsPermission()
//        }
    }

    private fun startBGDirectly() {
//        UtilFunctions.showToast(requireContext(), "Call Theme Enabled Directly")

        val serviceIntent = Intent(requireContext(), BackgroundCallService::class.java)
        requireContext().startService(serviceIntent)

        checkAndRequestAnswerPhoneCallsPermission()
    }

    private fun stopBackgroundService() {
        UtilFunctions.showToast(requireContext(), "Call Theme Disabled")

        val serviceIntent = Intent(requireContext(), BackgroundCallService::class.java)
        requireContext().stopService(serviceIntent)
    }

    private fun openYourActivity() {
        // Implement your logic for opening your activity
    }


}