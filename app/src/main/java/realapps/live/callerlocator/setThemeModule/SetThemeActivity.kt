package realapps.live.callerlocator.setThemeModule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieCompositionFactory
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callThemesModule.supportFunctions.BackgroundCallService
import realapps.live.callerlocator.callThemesModule.supportFunctions.SelectedTheme
import realapps.live.callerlocator.databinding.ActivitySetThemeBinding
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.SettingsData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class SetThemeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetThemeBinding

    var isDownloadClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.transparentStatusBar(this)
//        StatusBarUtils.setTopMargin(resources,binding.btBack)
//        StatusBarUtils.setTopMargin(resources,binding.btSelectContact)

        StatusBarUtils.setTopPadding(resources, binding.mainLayout)

        setTheme()
        onClickListeners()
    }

    private fun setTheme() {

        if (isFileDownloaded()) {
            binding.tvButton.text = "Apply Theme"
            binding.selectedTheme.visibility = View.VISIBLE
            checkStoragePermissions()
//            loadAnimationFromJsonFile(SelectedTheme.selectedTheme.themeName!!)
            //loadThemeFromDownload and Play
            binding.themeThumbNail.visibility = View.GONE
        } else {
            binding.tvButton.text = "Download Theme"

            Glide.with(this)
                .load(SelectedTheme.selectedTheme.thumbNail)
                .fitCenter()
                .into(binding.themeThumbNail)

            binding.themeThumbNail.visibility = View.VISIBLE
            binding.selectedTheme.visibility = View.GONE
        }

    }

    private fun onClickListeners() {
        binding.btSetTheme.setOnClickListener {

            val btText = binding.tvButton.text.toString()

            if (btText == "Apply Theme") {
                isDownloadClicked = false
                SettingsData.saveDefaultTheme(this, "${SelectedTheme.selectedTheme.themeName}.json")
                UtilFunctions.showToast(this, "Set as Default theme")
            } else {
                isDownloadClicked = true
                checkStoragePermissions()
            }

        }

        binding.btSelectContact.setOnClickListener {
            CallIntent.goToSelectContactsActivity(this, false, this)
        }

        binding.btBack.setOnClickListener {
            finish()
        }
    }


    private fun isFileDownloaded(): Boolean {
        val fileName = SelectedTheme.selectedTheme.themeName

        val file = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/Download/CallApp/$fileName.json"
        )

        if (!file.exists()) {
            return false
        }

        return true
    }

    private fun loadAnimationFromJsonFile(fileName: String) {
        val file = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/Download/CallApp/$fileName.json"
        )

        if (!file.exists()) {
            return
        }

        try {
            val inputStream = FileInputStream(file)

            val composition =
                LottieCompositionFactory.fromJsonInputStreamSync(inputStream, "$fileName").value
            inputStream.close()

            // Set the composition to LottieAnimationView
            binding.selectedTheme.setComposition(composition!!)
            binding.selectedTheme.playAnimation()
        } catch (e: Exception) {
            UtilFunctions.showToast(this, "Error loading animation")
        }
    }


    //////////////////////////////////////Download////////////////////


    private fun checkStoragePermissions() {
        checkAndroidVersionAndCallStorage()
    }

    val TAG = "Test"


    private val REQUEST_STORAGE_PERMISSION = 100

    private fun handleStoragePermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with your logic
            checkClickType()
        } else {
            UtilFunctions.showToast(this, "Storage Permission Denied")

        }
    }

    private fun checkAndroidVersionAndCallStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkClickType()
        } else {
            checkAllStoragePermissions()
        }
    }

    private fun downloadLayoutAsBitmap() {
        val themeUrl = SelectedTheme.selectedTheme.theme
        DownloadFileTask().execute(themeUrl)
    }

    private fun checkAllStoragePermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsToRequest = ArrayList<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_STORAGE_PERMISSION
            )
        } else {
           checkClickType()
        }
    }

    private fun checkClickType()
    {
        if (isDownloadClicked)
            downloadLayoutAsBitmap()
        else
            loadAnimationFromJsonFile(SelectedTheme.selectedTheme.themeName!!)

    }


    private inner class DownloadFileTask : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg urls: String?): Void? {
            binding.tvButton.text = "Downloading..."

            val fileUrl = urls[0]
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {


                    val root = File(
                        Environment.getExternalStorageDirectory().toString() + "/Download/CallApp"
                    )

                    if (!root.exists()) {
                        root.mkdirs()
                    }


                    val file = File(root, "${SelectedTheme.selectedTheme.themeName}.json")

                    val outputStream = FileOutputStream(file)
                    val inputStream = connection.inputStream

                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }

                    outputStream.close()
                    inputStream.close()
                    Log.d(TAG, "File downloaded successfully. Path: ${file.absolutePath}")
                } else {
                    Log.e(TAG, "Server returned HTTP response code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading file: ${e.message}")
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            Log.e(TAG, result.toString())
            isDownloadClicked=false
            setTheme()
            Toast.makeText(applicationContext, "Theme downloaded successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }


    ////////////////////Permissions///////////////////////////////

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
            REQUEST_STORAGE_PERMISSION -> handleStoragePermissionResult(grantResults)
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