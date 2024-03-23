package realapps.live.callerlocator.callThemesModule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callThemesModule.supportFunctions.BackgroundCallService
import realapps.live.callerlocator.callThemesModule.supportFunctions.CallerThemesAdapter
import realapps.live.callerlocator.callThemesModule.supportFunctions.CallerThemesUI
import realapps.live.callerlocator.callThemesModule.supportFunctions.SpacesItemDecoration
import realapps.live.callerlocator.databinding.FragmentCallThemesBinding
import realapps.live.callerlocator.setThemeModule.supportFunctions.SelectContactsAdapter
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.Contact
import realapps.live.callerlocator.zCommonFuntions.ContactManager
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.LoginData
import realapps.live.callerlocator.zSharedPreference.SettingsData


class CallThemesFragment : Fragment() {

    private lateinit var binding: FragmentCallThemesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_themes, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViews()
        getContacts()
        onClickListeners()
    }

    private lateinit var callerThemesUI: CallerThemesUI

    private fun initViews() {
        callerThemesUI = CallerThemesUI(requireContext(), binding)
//        callerThemesUI.setTopPadding(resources)
        StatusBarUtils.setTopPadding(resources,binding.callThemeLt)
        val callThemeStatus = SettingsData.getCallThemeStatus(requireContext())

        binding.btSwitchCallTheme.isChecked = callThemeStatus
        if(callThemeStatus)
            startBackgroundService()

    }

    private fun onClickListeners() {

        binding.btSwitchCallTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAndRequestPermissions()
                SettingsData.saveCallThemeStatus(requireContext(), true)
            } else {
                stopBackgroundService()
                SettingsData.saveCallThemeStatus(requireContext(), false)
            }
        }
    }


    private var allContacts: ArrayList<Contact> = ArrayList()

    private fun getContacts() {
        Log.e("Test", "Reading contacts")

        val contactManager = ContactManager(requireContext())
        val contacts = contactManager.getContacts()

        allContacts = contacts as ArrayList<Contact>

        initThemes()

    }

    private var themes = ArrayList<Int>()

    fun setThemes() {
        themes.add(R.raw.call_theme_1)
        themes.add(R.raw.call_theme_2)
        themes.add(R.raw.call_theme_3)
        themes.add(R.raw.call_theme_4)
    }

    private fun initThemes() {

        setThemes()

        val callerThemesAdapter = CallerThemesAdapter(
            requireContext(),
            allContacts,
            themes,
            ::onThemeSelected
        )

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)

        binding.rvThemes.apply {
            layoutManager = GridLayoutManager(
                context,
                3,  // Number of items in each row
                LinearLayoutManager.VERTICAL,
                false
            )
            addItemDecoration(SpacesItemDecoration(spacingInPixels, 3))
            adapter = callerThemesAdapter
        }

    }

    private fun onThemeSelected() {
        CallIntent.goToSetThemeActivity(requireContext(), false, requireActivity())
    }

    ///////////////////////Call Theme Permissions///////////////////////

    private val CALL_PERMISSION_REQUEST_CODE = 123
    private val ANSWER_PHONE_CALLS_REQUEST_CODE = 124
    private val REQUEST_OVERLAY_PERMISSION = 125
    private val REQUEST_CALL_LOG_PERMISSION = 126
    private val FOREGROUND_SERVICE_CONNECTED_DEVICE_PERMISSION_REQUEST_CODE = 1001


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
            checkForeGroundServicePermission()
//            startBackgroundService()
        }
    }

    private fun checkAndRequestOverlayPermission() {
        Log.e("Test", "72")
//        !Settings.canDrawOverlays(requireContext())

        Log.e("Test","OverLays ${Settings.canDrawOverlays(requireContext())}")
        if (!Settings.canDrawOverlays(requireContext())) {
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


    fun checkForeGroundServicePermission()
    {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL), FOREGROUND_SERVICE_CONNECTED_DEVICE_PERMISSION_REQUEST_CODE)
        } else {
            // Permission is already granted, proceed with starting the foreground service
            startBackgroundService()
//            startForegroundService()
        }
    }


    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(
            requireActivity(),
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
            FOREGROUND_SERVICE_CONNECTED_DEVICE_PERMISSION_REQUEST_CODE -> handleForeGroundServicePermissionResult(grantResults)
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
//            startBackgroundService()
            checkForeGroundServicePermission()
        } else {
            // Permission denied. Inform the user.
            Toast.makeText(
                requireContext(),
                "Permission denied. Not able to get Phone State.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleForeGroundServicePermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with your logic
            startBackgroundService()
        } else {
            // Permission denied. Inform the user.
            Toast.makeText(
                requireContext(),
                "Permission denied. ForeGroundPermission.",
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
        if (Settings.canDrawOverlays(requireContext())) {
            // Permission granted, proceed with your logic
            checkAndRequestPhoneStatePermission()
            openYourActivity()
        } else {
            // Permission not granted, show a message or take appropriate action
            Toast.makeText(
                requireContext(),
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
            Toast.makeText(requireContext(), "Call log permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBackgroundService() {
        // Implement your logic for starting the background service
//        UtilFunctions.showToast(requireContext(), "Call Theme Enabled")

        val serviceIntent = Intent(requireContext(), BackgroundCallService::class.java)
        requireContext().startService(serviceIntent)

    }

    private fun stopBackgroundService()
    {
        UtilFunctions.showToast(requireContext(), "Call Theme Disabled")

        val serviceIntent = Intent(requireContext(), BackgroundCallService::class.java)
        requireContext().stopService(serviceIntent)
    }

    private fun openYourActivity() {
        // Implement your logic for opening your activity
    }




}