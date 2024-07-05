package com.familylocation.mobiletracker.callSettingsModule

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.familylocation.mobiletracker.MyApplication
import com.familylocation.mobiletracker.callSettingsModule.supportFunctions.FlashlightManager
import com.familylocation.mobiletracker.databinding.ActivityCallFlashBinding
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.SettingsData

class CallFlashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallFlashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallFlashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Add these line to make status bar transparent
        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources,binding.mainLayout)

        MyApplication.getInstance().loadNativeAd(binding.rlAdplaceholder, this@CallFlashActivity)

        initViews()
        onClickListeners()

    }

    private fun initViews() {
        val status = SettingsData.getCallFlashLightStatus(this)

        binding.btSwitchCallFlshLight.isChecked = status

        val shakeStatus = SettingsData.getShakeStatus(this)
        binding.btSwitchShakeFlashing.isChecked = shakeStatus


        val lastFrequency = SettingsData.getCallLightFrequency(this)
        binding.tvFrequency.text = "${lastFrequency}ms"

        val progress = ((lastFrequency - 200) / 50)
        binding.flashFrequencyBar.progress = progress

    }

    private var isFlashOff = true

    private fun onClickListeners() {

        binding.btTestFlashLight.setOnClickListener {

            if (isFlashOff) {

                if (FlashlightManager.requestCameraPermission(this)) {

                    binding.btTestFlashLight.text = "Stop"

                    val progress = binding.flashFrequencyBar.progress
                    val timeInMilli = 200 + (progress * 50)
                    Log.e("Test", "$timeInMilli")
                    isFlashOff = false

                    FlashlightManager.toggleFlashlightBlink(this, timeInMilli.toLong())
                }
            } else {
                binding.btTestFlashLight.text = "Test"
                FlashlightManager.stopFlashlightBlink(this)
                isFlashOff = true
            }


        }

        binding.btSwitchCallFlshLight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

                if (FlashlightManager.requestCameraPermission(this)) {
                    UtilFunctions.showToast(this, "Call FlashLight Turned ON")

                    val frequency = binding.flashFrequencyBar.progress
                    val timeInMilli = 200 + (frequency * 50)

                    SettingsData.saveCallFlashLightStatus(this, true, timeInMilli)

                } else {
                    binding.btSwitchCallFlshLight.isChecked = false
                }

            } else {

                binding.btSwitchShakeFlashing.isChecked=false

                val lastFrequency = SettingsData.getCallLightFrequency(this)
                SettingsData.saveCallFlashLightStatus(this, false, lastFrequency)
                SettingsData.saveShakeStatus(this, false)

            }
        }

        binding.btSwitchShakeFlashing.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SettingsData.saveShakeStatus(this, true)
            } else {
                SettingsData.saveShakeStatus(this, false)
            }
        }

        binding.btBack.setOnClickListener {
            finish()
        }

        binding.flashFrequencyBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView when the seek bar value changes
//                updateTextView(progress)

                val timeInMilli = 200 + (progress * 50)
                binding.tvFrequency.text = "${timeInMilli}ms"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Implement if needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Implement if needed
            }
        })
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 123

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to use the flashlight

            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}