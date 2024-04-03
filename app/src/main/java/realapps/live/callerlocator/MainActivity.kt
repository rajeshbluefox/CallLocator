package realapps.live.callerlocator

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.LoginData

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StatusBarUtils.transparentStatusBar(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPostNotificationsPermission()
        } else {
            checkLoginStatusandNavigate()
        }
    }

    private fun checkLoginStatusandNavigate() {
        Handler(Looper.getMainLooper()).postDelayed({

            if (LoginData.getUserLoginStatus(this)) {

                CallIntent.goToControllingActivity(this,true,this)

            } else {
                CallIntent.goToLoginActivity(this, true, this)
            }

        }, 2000)
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
            }else{
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
                UtilFunctions.showToast(this,"Allow Notifications to proceed")
                // Permission denied
                // Handle the case where the user denies the permission
            }
        }
    }

}