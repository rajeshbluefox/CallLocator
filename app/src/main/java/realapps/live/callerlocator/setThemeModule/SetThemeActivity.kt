package realapps.live.callerlocator.setThemeModule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callThemesModule.supportFunctions.BackgroundCallService
import realapps.live.callerlocator.callThemesModule.supportFunctions.SelectedTheme
import realapps.live.callerlocator.databinding.ActivitySetThemeBinding
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.SettingsData

@AndroidEntryPoint
class SetThemeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetThemeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtils.transparentStatusBar(this)


        setTheme()
        onClickListeners()
    }

    private fun setTheme()
    {
        val selectedTheme =SelectedTheme.selectedThemeCount

        when(selectedTheme)
        {
            1 -> binding.selectedTheme.setAnimation(R.raw.call_theme_1)
            2 -> binding.selectedTheme.setAnimation(R.raw.call_theme_2)
            3 -> binding.selectedTheme.setAnimation(R.raw.call_theme_3)
            4 -> binding.selectedTheme.setAnimation(R.raw.call_theme_4)
        }
    }

    private fun onClickListeners() {
        binding.btSetTheme.setOnClickListener {
//            checkAndRequestPermissions()


            val selectedTheme =SelectedTheme.selectedThemeCount

            when(selectedTheme)
            {
                1 -> SettingsData.saveDefaultTheme(this,R.raw.call_theme_1)
                2 -> SettingsData.saveDefaultTheme(this,R.raw.call_theme_2)
                3 -> SettingsData.saveDefaultTheme(this,R.raw.call_theme_3)
                4 -> SettingsData.saveDefaultTheme(this,R.raw.call_theme_4)
            }

            UtilFunctions.showToast(this,"Set as Default theme")
        }

        binding.btSelectContact.setOnClickListener {
            CallIntent.goToSelectContactsActivity(this, false, this)
        }
    }

    private val CALL_PERMISSION_REQUEST_CODE = 123
    private val ANSWER_PHONE_CALLS_REQUEST_CODE = 124
    private val REQUEST_OVERLAY_PERMISSION = 125
    private val REQUEST_CALL_LOG_PERMISSION = 126

    private val phoneStatePermissions = arrayOf(android.Manifest.permission.READ_PHONE_STATE)
    private val overlayPermissions = arrayOf(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    private val callLogPermissions = arrayOf(Manifest.permission.READ_CALL_LOG)
    private val answerPhoneCallsPermissions = arrayOf(Manifest.permission.ANSWER_PHONE_CALLS)

    private fun checkAndRequestPermissions() {
        checkAndRequestCallLogPermission()
//        checkAndRequestAnswerPhoneCallsPermission()
//        checkAndRequestOverlayPermission()
//        checkAndRequestPhoneStatePermission()
    }

    private fun checkAndRequestPhoneStatePermission() {
        if (!arePermissionsGranted(phoneStatePermissions)) {
            requestPermissions(phoneStatePermissions, CALL_PERMISSION_REQUEST_CODE)
        } else {
            // Permission already granted, proceed with your logic
            startBackgroundService()
        }
    }

    private fun checkAndRequestOverlayPermission() {
        Log.e("Test", "72")
//        !arePermissionsGranted(overlayPermissions)
        if (!Settings.canDrawOverlays(this)) {
            Log.e("Test", "74")
            requestOverlayPermission()
//            requestPermissions(overlayPermissions, REQUEST_OVERLAY_PERMISSION)
        } else {
            Log.e("Test", "78")
            checkAndRequestPhoneStatePermission()

            // Permission already granted or not needed
            openYourActivity()
        }
    }

    private fun requestOverlayPermission() {
        Log.e("Test", "ROP")
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:realapps.live.callerlocator")
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
    }

    private fun checkAndRequestAnswerPhoneCallsPermission() {
        if (!arePermissionsGranted(answerPhoneCallsPermissions)) {
            requestPermission(answerPhoneCallsPermissions, ANSWER_PHONE_CALLS_REQUEST_CODE)
        } else {
            checkAndRequestOverlayPermission()
            // Permission already granted, proceed with your logic
        }
    }

    private fun checkAndRequestCallLogPermission() {
        if (!arePermissionsGranted(callLogPermissions)) {
            requestPermissions(callLogPermissions, REQUEST_CALL_LOG_PERMISSION)
        } else {
            checkAndRequestAnswerPhoneCallsPermission()
//            UtilFunctions.showToast(this, "Call Logs Permitted")
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PERMISSION_REQUEST_CODE -> handlePhoneStatePermissionResult(grantResults)
            ANSWER_PHONE_CALLS_REQUEST_CODE -> handleAnswerPhoneCallsPermissionResult(grantResults)
            REQUEST_CALL_LOG_PERMISSION -> handleCallLogPermissionResult(grantResults)
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
            startBackgroundService()
        } else {
            // Permission denied. Inform the user.
            Toast.makeText(
                this,
                "Permission denied. Not able to get Phone State.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleAnswerPhoneCallsPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with your logic
            checkAndRequestOverlayPermission()
        } else {
            // Permission denied. Inform the user or handle accordingly.
        }
    }

    private fun handleOverlayPermissionResult() {
        if (Settings.canDrawOverlays(this)) {
            // Permission granted, proceed with your logic
            checkAndRequestPhoneStatePermission()
            openYourActivity()
        } else {
            // Permission not granted, show a message or take appropriate action
            Toast.makeText(
                this,
                "Overlay permission not granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleCallLogPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkAndRequestAnswerPhoneCallsPermission()
//            UtilFunctions.showToast(this, "Call Logs Permitted")
        } else {
            // Permission denied, explain to user and handle accordingly
            Toast.makeText(this, "Call log permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBackgroundService() {
        // Implement your logic for starting the background service
        UtilFunctions.showToast(this, "Call Theme Enabled")

        val serviceIntent = Intent(this, BackgroundCallService::class.java)
        startService(serviceIntent)

    }

    private fun openYourActivity() {
        // Implement your logic for opening your activity
    }

}