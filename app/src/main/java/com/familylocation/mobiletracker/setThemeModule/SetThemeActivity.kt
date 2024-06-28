package com.familylocation.mobiletracker.setThemeModule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieCompositionFactory
import com.bumptech.glide.Glide
import com.familylocation.mobiletracker.MyApplication
import dagger.hilt.android.AndroidEntryPoint
import com.familylocation.mobiletracker.callThemesModule.supportFunctions.SelectedTheme
import com.familylocation.mobiletracker.databinding.ActivitySetThemeBinding
import com.familylocation.mobiletracker.phoneToolsModule.PhoneToolActivity
import com.familylocation.mobiletracker.zCommonFuntions.CallIntent
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.SettingsData
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

            val fileName = SelectedTheme.selectedTheme.themeName + ".json"

            val defaultThemeId = SettingsData.getDefaultTheme(this)
            Log.e("Test", "L1 $defaultThemeId")

            if (fileName.equals(defaultThemeId)) {
                binding.tvButton.text = "Theme Applied as Default"
            } else {
                binding.tvButton.text = "Apply Theme"
            }

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
                binding.tvButton.text = "Theme Applied as Default"

            } else if (btText == "Theme Applied as Default") {
                UtilFunctions.showToast(this, "Already set as Default Theme")
            } else {
                isDownloadClicked = true
                checkStoragePermissions()
            }

        }

        binding.btSelectContact.setOnClickListener {

            val intent = Intent(
                this@SetThemeActivity,
                SelectContactsActivity::class.java
            )

            MyApplication.getApplication()
                .displayInterstitialAds(this@SetThemeActivity, intent, false,layoutInflater,this)


//            CallIntent.goToSelectContactsActivity(this, false, this)
        }

        binding.btBack.setOnClickListener {
            finish()
        }
    }


    private fun isFileDownloaded(): Boolean {
        val fileName = SelectedTheme.selectedTheme.themeName

        val file = File(
            this.getExternalFilesDir(null)
                .toString() + "/Download/CallApp/$fileName.json"
        )

        if (!file.exists()) {
            return false
        }

        return true
    }

    private fun loadAnimationFromJsonFile(fileName: String) {

//        Environment.getExternalStorageDirectory()

        val file = File(
            this.getExternalFilesDir(null)
                .toString() + "/Download/CallApp/$fileName.json"
        )

//        if (!file.exists()) {
//            UtilFunctions.showToastLong(this, "File already exists")
//            return
//        }

        try {
            val inputStream = FileInputStream(file)

            val composition =
                LottieCompositionFactory.fromJsonInputStreamSync(inputStream, "$fileName").value
            inputStream.close()

            // Set the composition to LottieAnimationView
            binding.selectedTheme.setComposition(composition!!)
            binding.selectedTheme.playAnimation()
        } catch (e: Exception) {
            binding.tvError.text = e.message.toString()
            UtilFunctions.showToastLong(this, "$fileName ${e.message}")
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

//        checkAllStoragePermissions()

    }

    private fun downloadLayoutAsBitmap() {
        val themeUrl = SelectedTheme.selectedTheme.theme
        DownloadFileTask().execute(themeUrl)
    }


    private fun checkAllStoragePermissions() {


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)

        val permissions = if (false) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )
        } else {

            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

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

    private fun checkClickType() {
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
                        getExternalFilesDir(null).toString() + "/Download/CallApp"
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
            isDownloadClicked = false
            setTheme()
            Toast.makeText(applicationContext, "Theme downloaded successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }

}