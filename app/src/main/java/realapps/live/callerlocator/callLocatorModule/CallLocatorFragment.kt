package realapps.live.callerlocator.callLocatorModule

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callLocatorModule.apiFunctions.CallLocatorApiFunctions
import realapps.live.callerlocator.callLocatorModule.apiFunctions.CallLocatorViewModel
import realapps.live.callerlocator.callLocatorModule.modalClass.LocationData
import realapps.live.callerlocator.callLocatorModule.supportFunctions.CallLocatorFragmentUI
import realapps.live.callerlocator.callLocatorModule.supportFunctions.EnteredNumber
import realapps.live.callerlocator.callLocatorModule.supportFunctions.InviteUserDialog
import realapps.live.callerlocator.callLocatorModule.supportFunctions.PermissionResultListener
import realapps.live.callerlocator.callLocatorModule.supportFunctions.SendRequestDialog
import realapps.live.callerlocator.databinding.FragmentCallLocatorBinding
import realapps.live.callerlocator.loginModule.apiFunctions.LoginViewModel
import realapps.live.callerlocator.loginModule.supportFunctions.LoginAPIFunctions
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.Contact
import realapps.live.callerlocator.zCommonFuntions.ContactManager
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.LoginData

@AndroidEntryPoint
class CallLocatorFragment : Fragment(), OnMapReadyCallback, PermissionResultListener {

    private lateinit var mMap: GoogleMap

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var isLocationFetched = false

    private lateinit var binding: FragmentCallLocatorBinding

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var callLocatorViewModel: CallLocatorViewModel
    private lateinit var callLocatorFragmentUI: CallLocatorFragmentUI
    private lateinit var loginAPIFunctions: LoginAPIFunctions
    private lateinit var callLocatorApiFunctions: CallLocatorApiFunctions
    private lateinit var inviteUserDialog: InviteUserDialog
    private lateinit var sendRequestDialog: SendRequestDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_call_locator, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        askPermissions()

        initViews()
        editTextListener()
        setMapView()
        onClickListeners()

    }

    private fun initViews() {
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        callLocatorViewModel = ViewModelProvider(this)[CallLocatorViewModel::class.java]

        inviteUserDialog = InviteUserDialog(layoutInflater, requireContext())
        sendRequestDialog = SendRequestDialog(layoutInflater, requireContext(), ::sendFriendRequest)

        callLocatorFragmentUI = CallLocatorFragmentUI(requireContext(), binding)
        callLocatorFragmentUI.setTopPadding(resources)

        loginAPIFunctions = LoginAPIFunctions(
            requireContext(),
            requireActivity(),
            viewLifecycleOwner,
            loginViewModel,
            ::findUserExistence,
            postNewUserObserverCallBack = {}
        )
        callLocatorApiFunctions = CallLocatorApiFunctions(
            requireContext(),
            requireActivity(),
            viewLifecycleOwner,
            callLocatorViewModel,
            ::sendFriendRequestResponse,
           getFriendRequestObserverCB = {}
        )


    }

    private fun sendFriendRequest() {
        val fromNumber = LoginData.getUserPhone(requireContext())
        val toNumber = binding.etSearch.text.toString()

        callLocatorApiFunctions.sendFriendRequest(fromNumber, toNumber)
    }

    private fun sendFriendRequestResponse(status: Boolean) {
        if (status)
            UtilFunctions.showToast(requireContext(), "Req Success")
        else
            UtilFunctions.showToast(requireContext(), "Req Failed")

    }

    private fun findUserExistence(status: Boolean) {
        if (status) {
            sendRequestDialog.openSendRequestSheet()
        } else {
            inviteUserDialog.openInviteUserSheet()
        }
    }


    private fun editTextListener() {

        binding.etSearch.doOnTextChanged { text, _, _, _ ->

            Log.e("Test", text.toString())

            if (text!!.isEmpty()) {
                binding.btClear.visibility = View.GONE
                binding.btSearch.visibility = View.GONE

                callLocatorFragmentUI.toggleContactLayout(false)
            } else {
                binding.btClear.visibility = View.VISIBLE
                binding.btSearch.visibility = View.VISIBLE

            }

        }
    }


    private var allContacts: ArrayList<Contact> = ArrayList()

    private fun getContacts() {
        Log.e("Test", "Reading contacts")

        val contactManager = ContactManager(requireContext())
        val contacts = contactManager.getContacts()

        allContacts = contacts as ArrayList<Contact>

        for (contact in contacts) {
            Log.e("Test", "Name: ${contact.name}, Number: ${contact.number}")
        }
    }

    private fun findContact(text: String) {

        for (localItems in allContacts) {
            if (localItems.name.lowercase().startsWith(text)) {

            }
        }

    }

    private fun setMapView() {
        // Create a SupportMapFragment
        val mapFragment = SupportMapFragment.newInstance()

        // Add the SupportMapFragment to the container
        childFragmentManager.beginTransaction()
            .replace(R.id.map, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun onClickListeners() {

        binding.btLocation.setOnClickListener {
            getLocation()
        }

        binding.btSearch.setOnClickListener {
            callLocatorFragmentUI.setContactNumber(binding.etSearch.text.toString())
            UtilFunctions.hideKeyboard(binding.etSearch)

            val mPhoneNumber = binding.etSearch.text.toString()
            if (mPhoneNumber.length == 10) {
                loginAPIFunctions.findUserExistence(phoneNumber = mPhoneNumber)
            }
        }

        binding.btCall.setOnClickListener {
            checkCallPermission(true)
        }

        binding.btClear.setOnClickListener {
            callLocatorFragmentUI.clearSearch()
        }

        binding.icSearch.setOnClickListener {
            CallIntent.goToFriendRequestActivity(requireContext(),false,requireActivity())
        }
    }

    var isCallPermissionGiven = false

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        if (isCallPermissionGiven)
            getLocation()
    }

    private fun getLocation() {
        if (checkLocationPermission()) {
            fetchLocation()
        }

    }

    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun fetchLocation() {
        Log.e("Test", "Fetch Location")

        if (isLocationEnabled()) {

            if (!isLocationListening)
                listenForLocation()


            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                val location: Location? = task.result
                if (location != null) {

                    onLocationFetched(location)
                } else {
                    Log.e("Test", "No Location Found")
                }
            }
        } else {
//                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // Set the desired interval for location updates in milliseconds
            fastestInterval = 5000 // Set the fastest interval for location updates in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Check for location permissions before requesting updates
        if (checkLocationPermission()) {
            mFusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper - can be null for the main thread */
            )
        } else {
            // Handle the case where location permissions are not granted
            // You might want to request permissions here or handle it accordingly
        }
    }

    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    var isLocationListening = false

    fun listenForLocation() {

        Log.e("Test", "Listening for Location")

        isLocationListening = true
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Initialize LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.lastLocation?.let { location ->
                    // Handle the fetched location here
                    onLocationFetched(location)
                }
            }
        }

        requestLocationUpdates()
    }

    private fun onLocationFetched(location: Location) {
        // Handle the fetched location, for example, show a toast

        isLocationFetched = true

        LocationData.latitude = location.latitude
        LocationData.longitude = location.longitude

        val locationLatLng = LatLng(
            location.latitude,
            location.longitude
        ) // San Francisco coordinates

        mMap.addMarker(
            MarkerOptions().position(locationLatLng)
                .title("Location")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 18f))

        stopLocationUpdates()
        isLocationListening = false
    }

    private fun isLocationEnabled(): Boolean {

        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //Permissions
    private val CONTACTS_PERMISSION_CODE = 1001
    private val CALL_PERMISSION_REQUEST_CODE = 1002
    private val permissionId = 1003


    private val contactPermissions = arrayOf(Manifest.permission.READ_CONTACTS)
    private val callPermissions = arrayOf(
        android.Manifest.permission.CALL_PHONE,
        android.Manifest.permission.READ_PHONE_STATE
    )
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun askPermissions() {
//        checkLocationPermission()

        checkContactPermission()
//        checkCallPermission()
    }

    private fun checkContactPermission() {
        // Check if the permission is granted
        if (arePermissionsGranted(contactPermissions)) {
            // Permission is already granted, you can proceed with contact retrieval
            checkCallPermission(false)
            getContacts()
        } else {
            // Permission is not granted, request it
            requestPermission(contactPermissions, CONTACTS_PERMISSION_CODE)
        }
    }

    private fun checkCallPermission(isForCall: Boolean) {
        // Check if CALL_PHONE and READ_PHONE_STATE permissions are granted
        if (arePermissionsGranted(callPermissions)) {
            // Both permissions are granted, proceed with the call
            isCallPermissionGiven = true
            if (isForCall)
                UtilFunctions.callNumber(requireContext(), EnteredNumber.phoneNumber)
            else
                checkLocationPermission()
        } else {
            // Request the necessary permissions
            requestPermission(callPermissions, CALL_PERMISSION_REQUEST_CODE)
        }
    }

    private fun checkLocationPermission(): Boolean {
        // Check if location permissions are granted
        if (arePermissionsGranted(locationPermissions)) {
            Log.e("Test", "281")
//            checkContactPermission()
            return true
        } else {
            // Request location permissions
            Log.e("Test", "285")
            requestPermission(locationPermissions, permissionId)
        }

        return false
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        // Check if all permissions in the array are granted
        return permissions.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermission(permissions: Array<String>, requestCode: Int) {
        requestPermissions(permissions, requestCode)
    }


    private fun handleContactPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with contact retrieval
            checkCallPermission(false)
            getContacts()
        } else {
            // Permission denied, inform the user
            Toast.makeText(
                requireContext(),
                "Permission denied. Cannot retrieve contacts.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleCallPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//            UtilFunctions.callNumber(requireContext(), EnteredNumber.phoneNumber)
//            UtilFunctions.showToast(requireContext(),"Calls Granted")
            checkContactPermission()
        } else {
            // Permission denied, inform the user
            Toast.makeText(
                requireContext(),
                "Permission denied. Cannot make a call.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleLocationPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("Test", "351")

            isCallPermissionGiven = true

            fetchLocation()
//            checkContactPermission()
        }
        // Handle the case where location permission is denied if needed
    }

    private val permissionResultListener = this // Reference to the interface implementation

    override fun onPermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CONTACTS_PERMISSION_CODE -> handleContactPermissionResult(grantResults)
            CALL_PERMISSION_REQUEST_CODE -> handleCallPermissionResult(grantResults)
            permissionId -> handleLocationPermissionResult(grantResults)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionResultListener.onPermissionResult(requestCode, permissions, grantResults)
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

    }


}