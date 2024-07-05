package com.familylocation.mobiletracker.callThemesModule

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.familylocation.mobiletracker.databinding.ActivityCallSettingBinding


class CallSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun checkAndRequestOverlayPermission() {
        Log.e("Test", "72")

//        Log.e("Test", "OverLays ${Settings.canDrawOverlays(requireContext())}")
        if (!Settings.canDrawOverlays(this)) {
            Log.e("Test", "74")
//            requestOverlayPermission()
//            requestPermissions(overlayPermissions, REQUEST_OVERLAY_PERMISSION)
        } else {
            Log.e("Test", "RequestOverlay Permission Already Granted 3")

//            checkAndRequestOverlayPermissionRunning()

            // Permission already granted or not needed
//            openYourActivity()
        }
    }



}