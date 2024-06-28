package com.familylocation.mobiletracker.profileModule


import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieCompositionFactory
import com.familylocation.mobiletracker.MyApplication
import dagger.hilt.android.AndroidEntryPoint
import com.familylocation.mobiletracker.databinding.ActivityProfileBinding
import com.familylocation.mobiletracker.loginModule.LogoutDialog
import com.familylocation.mobiletracker.zCommonFuntions.CallIntent
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.LoginData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var logoutDialog: LogoutDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Add these line to make status bar transparent
        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources,binding.mainLayout)

        MyApplication.getInstance().loadNativeAd(binding.rlAdplaceholder, this@ProfileActivity)


        initViews()
        onClickListeners()
    }

    private fun initViews() {
        logoutDialog = LogoutDialog(layoutInflater, this, ::onLogoutClicked)

        binding.tvPhoneNumber.text=LoginData.getUserPhone(this)
    }

    private fun onLogoutClicked() {
        LoginData.saveUserLoginStatus(this, false)
        CallIntent.goToLoginActivity(this, true, this)
    }

    private fun onClickListeners() {
        binding.btBack.setOnClickListener {
            finish()
        }

        binding.btLogout.setOnClickListener {
            logoutDialog.openLogoutDialog()
        }

        binding.btDownload.setOnClickListener {
            checkStoragePermissions()
        }

        binding.btLoad.setOnClickListener {
            loadAnimationFromJsonFile("downloaded_file.json")
        }
    }

    private fun checkStoragePermissions() {
        checkAndroidVersionAndCallStorage()
    }

    //    private val PERMISSION_REQUEST_CODE = 1
    private val TAG = "MainActivity"
    private val JSON_URL =
        "https://gundasupermarket.com/Caller_Locator_App/animations/call_theme_1.json"
    private val FOLDER_NAME = "animations"

    private val REQUEST_STORAGE_PERMISSION = 100

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can access storage
                    Log.e("Test", "Permission Granted")

                    downloadLayoutAsBitmap()
//                    downloadQRCode()
                } else {
                    Log.e("Test", "Permission Denied")

                    UtilFunctions.showToast(this, "Storage Permission Denied")

                    // Permission denied, handle this case (e.g., show a message)
                }
            }
        }
    }

    private fun checkAndroidVersionAndCallStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Do something for lollipop and above versions
            Log.e("Test", "Version 13 and above")

            downloadLayoutAsBitmap()
//            downloadQRCode()
        } else {
            // do something for phones running an SDK before lollipop
            checkAllStoragePermissions()
        }
    }

    private fun downloadLayoutAsBitmap() {
        DownloadFileTask().execute(JSON_URL)
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
            // Permissions are already granted, you can access storage

            Log.e("Test", "Permission already Granted")

//            downloadQRCode()
            downloadLayoutAsBitmap()
        }
    }


    private inner class DownloadFileTask : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg urls: String?): Void? {
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


                    val file = File(root, "downloaded_file.json")

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
            Toast.makeText(applicationContext, "File downloaded successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun loadAnimationFromJsonFile(fileName: String) {
//        downloaded_file.json
        val file = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/Download/CallApp/$fileName"
        )

        if (!file.exists()) {
            Log.e(TAG, "File does not exist")
            return
        }

        try {
            val inputStream = FileInputStream(file)

            val composition = LottieCompositionFactory.fromJsonInputStreamSync(inputStream, "$fileName").value
            inputStream.close()

            // Set the composition to LottieAnimationView
            binding.lottieAnimationView.setComposition(composition!!)
            binding.lottieAnimationView.playAnimation()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading animation: ${e.message}")
        }
    }

}