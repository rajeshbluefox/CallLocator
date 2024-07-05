package com.familylocation.mobiletracker.callSettingsModule.supportFunctions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object FlashlightManager {

    private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    private var flashlightOn = false
    private var blinkHandler: Handler? = null
    private var blinkRunnable: Runnable? = null

    fun requestCameraPermission(context: Context):Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
            return false
        } else {
//            turnOnFlashlight(context)
//            FlashlightManager.toggleFlashlightBlink(context,250)
        }

        return true
    }

    fun toggleFlashlightBlink(context: Context, intervalMillis: Long) {
        if (blinkHandler == null) {
            blinkHandler = Handler()
            blinkRunnable = object : Runnable {
                override fun run() {
                    toggleFlashlight(context)
                    blinkHandler?.postDelayed(this, intervalMillis)
                }
            }
        }else {
            stopFlashlightBlink(context)
        }

        if (flashlightOn) {
            blinkHandler?.removeCallbacks(blinkRunnable!!)
            turnOffFlashlight(context)
        } else {
            blinkHandler?.post(blinkRunnable!!)
        }
    }

    fun stopFlashlightBlink(context: Context) {
        blinkHandler?.removeCallbacks(blinkRunnable!!)
        turnOffFlashlight(context)
    }

    private fun toggleFlashlight(context: Context) {
        if (flashlightOn) {
            turnOffFlashlight(context)
        } else {
            turnOnFlashlight(context)
        }
    }

    private fun turnOnFlashlight(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, true)
            flashlightOn = true
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun turnOffFlashlight(context: Context) {
        if (flashlightOn) {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, false)
                flashlightOn = false
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }
}