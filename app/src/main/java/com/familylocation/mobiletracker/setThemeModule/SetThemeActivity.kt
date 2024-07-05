package com.familylocation.mobiletracker.setThemeModule

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
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
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callThemesModule.supportFunctions.SelectedTheme
import com.familylocation.mobiletracker.databinding.ActivitySetThemeBinding
import com.familylocation.mobiletracker.setThemeModule.supportFunctions.BottomSheetActionsFragment
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.SettingsData
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class SetThemeActivity : AppCompatActivity(), BottomSheetActionsFragment.BottomSheetListener {

    private lateinit var binding: ActivitySetThemeBinding

    private var isDownloadClicked = false

    private lateinit var bottomSheetFragment: BottomSheetActionsFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example usage to show bottom sheet
        bottomSheetFragment = BottomSheetActionsFragment()
        bottomSheetFragment.setBottomSheetListener(this)

        StatusBarUtils.transparentStatusBar(this)
//        StatusBarUtils.setTopMargin(resources,binding.btBack)
//        StatusBarUtils.setTopMargin(resources,binding.btSelectContact)

        StatusBarUtils.setTopPadding(resources, binding.mainLayout)

//        setTheme()
        setWallpaper()
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

    private fun setWallpaper() {
        if (isWallpaperDownloaded()) {
            Log.e("Test", "Wallpaper Downloaded")


            val fileName = SelectedTheme.selectedTheme.thumbNail
            val defaultThemeId = SettingsData.getDefaultTheme(this)

//            if (fileName == defaultThemeId) {
//                binding.tvButton.text = "Wallpaper Applied"
//            } else {
//                binding.tvButton.text = "Set Wallpaper"
//            }

            binding.tvButton.text = "Set Wallpaper"

//            binding.selectedTheme.visibility = View.VISIBLE

            Glide.with(this)
                .load(SelectedTheme.selectedTheme.thumbNail)
                .into(binding.themeThumbNail)

//            checkStoragePermissions()
//            loadAnimationFromJsonFile(SelectedTheme.selectedTheme.themeName!!)
            //loadThemeFromDownload and Play
//            binding.themeThumbNail.visibility = View.GONE

        } else {
            Log.e("Test", "Wallpaper Not Downloaded")
            binding.tvButton.text = "Download Wallpaper"

            Glide.with(this)
                .load(SelectedTheme.selectedTheme.thumbNail)
                .fitCenter()
                .into(binding.themeThumbNail)

            binding.themeThumbNail.visibility = View.VISIBLE
            binding.selectedTheme.visibility = View.GONE
        }
    }

    private fun onClickListeners() {
//        binding.btSetTheme.setOnClickListener {
//
//            val btText = binding.tvButton.text.toString()
//
//            if (btText == "Apply Theme") {
//                isDownloadClicked = false
//                SettingsData.saveDefaultTheme(this, "${SelectedTheme.selectedTheme.themeName}.json")
//                UtilFunctions.showToast(this, "Set as Default theme")
//                binding.tvButton.text = "Theme Applied as Default"
//
//            } else if (btText == "Theme Applied as Default") {
//                UtilFunctions.showToast(this, "Already set as Default Theme")
//            } else {
//                isDownloadClicked = true
//                checkStoragePermissions()
//            }
//
//        }

        binding.btSetTheme.setOnClickListener {

            val btText = binding.tvButton.text.toString()

            if (btText == "Set Wallpaper") {
                isDownloadClicked = false

//                binding.tvButton.text = "Wallpaper Applied"
//                SettingsData.saveDefaultTheme(this, "${SelectedTheme.selectedTheme.thumbNail}")

//                UtilFunctions.showToast(this, "Set as Wallpaper")
//                binding.tvButton.text = "Wallpaper Applied"

//                val fileName = SelectedTheme.selectedTheme.thumbNail


                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)


//                setWallpaperFromFile(file)

            } else if (btText == "Wallpaper Applied") {
                UtilFunctions.showToast(this, "Already set as Wallpaper")
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
                .displayInterstitialAds(this@SetThemeActivity, intent, false, layoutInflater, this)

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

    private fun isWallpaperDownloaded(): Boolean {
//        val fileName = SelectedTheme.selectedTheme.thumbNail

        val url = SelectedTheme.selectedTheme.thumbNail
        val fileName = url?.substringAfterLast("/")

//        val fileName = "${SelectedTheme.selectedTheme.themeName}.json"


        val file = File(
            this.getExternalFilesDir(null)
                .toString() + "/Download/CallAppWallpaper/$fileName"
        )

        Log.e("Test", "isWallpaperDownloaded ${file.absolutePath}")

        if (!file.exists()) {
            return false
        }

        return true
    }

    private fun loadAnimationFromJsonFile(fileName: String) {

//        Environment.getExternalStorageDirectory()

        val file = File(
            this.getExternalFilesDir(null)
                .toString() + "/Download/CallAppWallpaper/$fileName.json"
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
//        val themeUrl = SelectedTheme.selectedTheme.theme
        val wallpaperUrl = SelectedTheme.selectedTheme.thumbNail
        Log.e("Test", "Thumbnail Link : $wallpaperUrl")
        DownloadFileTask().execute(wallpaperUrl)
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


//    private inner class DownloadFileTask :  AsyncTask<String, Void, Void>() {

    private inner class DownloadFileTask : AsyncTask<String, Int, File?>() {

        private var successColor: Int =
            ContextCompat.getColor(binding.tvButton.context, R.color.green_progress_color)
        private var downloadingColor: Int =
            ContextCompat.getColor(binding.tvButton.context, R.color.blue_progress_color)
        private var failureColor: Int =
            ContextCompat.getColor(binding.tvButton.context, R.color.red_progress_color)


        override fun onPreExecute() {
            super.onPreExecute()
            // Change background color to indicate progress
            animateColorChange(downloadingColor)
//        binding.tvButton.setBackgroundResource(R.drawable.progress_background_downloading)
            binding.tvButton.text = "Downloading..."
        }

        override fun doInBackground(vararg urls: String?): File? {
            val imageUrl = urls[0]
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                // Check if the connection was successful
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {

//                    val rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    val wallpaperDir = File(rootDir, "CallAppWallpaper")

                    val wallpaperDir = File(
                        getExternalFilesDir(null).toString() + "/Download/CallAppWallpaper"
                    )

                    if (!wallpaperDir.exists()) {
                        wallpaperDir.mkdirs()
                    }

//                    val fileName = "wallpaper_${System.currentTimeMillis()}.jpg"

                    val url1 = SelectedTheme.selectedTheme.thumbNail
                    val fileName = url1?.substringAfterLast("/")

//                    val fileName = "${SelectedTheme.selectedTheme.themeName}.json"


                    val file = File(wallpaperDir, fileName)
                    val outputStream = FileOutputStream(file)
                    val inputStream = connection.inputStream

                    val totalLength = connection.contentLength
                    var downloadedLength = 0

                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)

                        downloadedLength += bytesRead
                        publishProgress((downloadedLength * 100) / totalLength)
                    }

                    outputStream.close()
                    inputStream.close()

                    Log.e("Test", "Image downloaded successfully. Path: ${file.absolutePath}")
                    return file
                } else {
                    Log.e(TAG, "Server returned HTTP response code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("Test", "Error downloading image: ${e.message}")
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            // Update progress text or any UI related to progress
//            binding.tvButton.text = "Downloading... ${values[0]}%"
            binding.tvButton.text = "Downloading..."
        }

        override fun onPostExecute(result: File?) {
            super.onPostExecute(result)

            if (result != null) {
                animateColorChange(successColor)
                setWallpaper()
            } else {
                animateColorChange(failureColor)
                binding.tvButton.text = "Download Failed"
            }

//        result?.let {
//            // Handle successful download
//            setWallpaper()
//            Log.e("Test", "Image download completed: ${result.absolutePath}")
//        }
        }

        private fun animateColorChange(targetColor: Int) {
            val currentColor =
                (binding.tvButton.background as? ColorDrawable)?.color ?: downloadingColor
            val colorAnimator = ObjectAnimator.ofObject(
                binding.tvButton,
                "backgroundColor",
                ArgbEvaluator(),
                currentColor,
                targetColor
            ).apply {
                duration = 300 // Animation duration in milliseconds
                start()
            }
        }


//        override fun doInBackground(vararg urls: String?): Void? {
//            binding.tvButton.text = "Downloading..."
//
//            val fileUrl = urls[0]
//            try {
//                val url = URL(fileUrl)
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//                connection.connect()
//
//                val responseCode = connection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//
//
//                    val root = File(
//                        getExternalFilesDir(null).toString() + "/Download/CallApp"
//                    )
//
//                    if (!root.exists()) {
//                        root.mkdirs()
//                    }
//
//
////                    val file = File(root, "${SelectedTheme.selectedTheme.themeName}.json")
//
//                    val url = SelectedTheme.selectedTheme.thumbNail
//                    val fileName = url?.substringAfterLast("/")
//
//                    val file = File(root, fileName!!)
//
//
//                    val outputStream = FileOutputStream(file)
//                    val inputStream = connection.inputStream
//
//                    val buffer = ByteArray(1024)
//                    var bytesRead: Int
//                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                        outputStream.write(buffer, 0, bytesRead)
//                    }
//
//                    outputStream.close()
//                    inputStream.close()
//                    Log.e("Test", "File downloaded successfully. Path: ${file.absolutePath}")
//                } else {
//                    Log.e(TAG, "Server returned HTTP response code: $responseCode")
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Error downloading file: ${e.message}")
//            }
//            return null
//        }
//
//        override fun onPostExecute(result: Void?) {
//            super.onPostExecute(result)
//            Log.e(TAG, result.toString())
//            isDownloadClicked = false
////            setTheme()

//            Toast.makeText(applicationContext, "Wallpaper downloaded successfully", Toast.LENGTH_SHORT)
//                .show()
//        }


    }


///////////////////////////////Set Wallpaper/////////////////////////////////////

    private fun setWallpaperFromFile(setAsHome: Boolean, setAsLock: Boolean) {
        try {

            val url = SelectedTheme.selectedTheme.thumbNail
            val fileName = url?.substringAfterLast("/")
//            val fileName = "${SelectedTheme.selectedTheme.themeName}.json"

            val file = File(
                getExternalFilesDir(null)
                    .toString() + "/Download/CallAppWallpaper/$fileName"
            )

            Log.e("Test","Wallpaper path: ${file.absolutePath}")

            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val wallpaperManager = WallpaperManager.getInstance(this)

            // Set wallpaper for home screen
            if (setAsHome) {
                wallpaperManager.setBitmap(bitmap)
                UtilFunctions.showToast(this, "Home Wallpaper Applied")
            }


            if (setAsLock) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    UtilFunctions.showToast(this, "Lock Screen Wallpaper Applied")

                } else {
                    UtilFunctions.showToast(
                        this,
                        "Setting lock screen wallpaper is not supported on this device"
                    )
                }
            }

            // Set wallpaper for lock screen
//            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)

//            Toast.makeText(applicationContext, "Wallpaper set successfully", Toast.LENGTH_SHORT)
//                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error setting wallpaper: ${e.message}")
            Toast.makeText(applicationContext, "Failed to set wallpaper", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSetAsWallpaperClicked() {

        setWallpaperFromFile(true, false)
    }

    override fun onSetAsLockScreenClicked() {
        setWallpaperFromFile(false, true)
    }

    override fun onSetAsBothClicked() {
        binding.tvButton.text = "Wallpaper Applied"
        SettingsData.saveDefaultTheme(this, "${SelectedTheme.selectedTheme.thumbNail}")

        setWallpaperFromFile(true, true)
    }


}