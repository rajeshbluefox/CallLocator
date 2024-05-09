package realapps.live.callerlocator.callThemesModule

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import realapps.live.callerlocator.databinding.ActivityCallSettingBinding


class CallSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestRole()
//        requestSetDefaultDialer()
        // Check and request necessary permissions
//        if (!checkPermissions()) {
//            requestPermissions()
//        } else {
//            requestSetDefaultDialer()
//        }
    }

    private val REQUEST_ID = 1

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestRole() {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        startActivityForResult(intent, REQUEST_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                binding.status.text=" Default App"

                // Your app is now the default dialer app
            } else {
                // Your app is not the default dialer app
                binding.status.text="Not Default"
            }
        }
    }

    private val REQUEST_PERMISSION_CODE = 123

//    private fun checkPermissions(): Boolean {
//        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(this,
//            arrayOf(
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.MODIFY_PHONE_STATE,
//                Manifest.permission.READ_CALL_LOG,
//                Manifest.permission.CALL_PHONE
//            ),
//            REQUEST_PERMISSION_CODE
//        )
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                // All permissions granted
//                requestSetDefaultDialer()
//            } else {
//                // Permissions not granted, handle accordingly
//                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun requestSetDefaultDialer() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
//            startActivity(intent)
//        } else {
//            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
//            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, "Unable to open settings to set default dialer.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}