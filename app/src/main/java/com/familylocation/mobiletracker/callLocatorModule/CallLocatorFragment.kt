package com.familylocation.mobiletracker.callLocatorModule

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.ContactsContract
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.familylocation.mobiletracker.MainActivity
import com.familylocation.mobiletracker.MyApplication
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callLocatorModule.apiFunctions.CallLocatorApiFunctions
import com.familylocation.mobiletracker.callLocatorModule.apiFunctions.CallLocatorViewModel
import com.familylocation.mobiletracker.callLocatorModule.modalClass.LocationData
import com.familylocation.mobiletracker.callLocatorModule.modalClass.MyFriendsDataItem
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.CallLocatorFragmentUI
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.EnteredNumber
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.InviteUserDialog
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.MyFriendsAdapter
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.PermissionResultListener
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.SelectedFriend
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.SendRequestDialog
import com.familylocation.mobiletracker.databinding.FragmentCallLocatorBinding
import com.familylocation.mobiletracker.loginModule.apiFunctions.LoginViewModel
import com.familylocation.mobiletracker.loginModule.supportFunctions.LoginAPIFunctions
import com.familylocation.mobiletracker.zCommonFuntions.Contact
import com.familylocation.mobiletracker.zCommonFuntions.ContactManager
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zDatabase.BlockedContactsDBHelper
import com.familylocation.mobiletracker.zSharedPreference.LoginData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale


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

    private lateinit var dbHelper: BlockedContactsDBHelper

    private val coroutineScope =
        CoroutineScope(Dispatchers.Main) // Dispatchers.Main represents the main (UI) thread

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_call_locator, container, false)

        // Initialize your dbHelper here
        dbHelper = BlockedContactsDBHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        askPermissions()

        initViews()
        editTextListener()

        onClickListeners()

        coroutineScope.launch {
            setMapView()
        }

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
            getFriendRequestObserverCB = { _, _ -> },
            respondToFriendRequestObserver = {},
            ::myFriendsResponse
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

        binding.btAddNumberPu.setOnClickListener {
            val mPhoneNumber = binding.etSearch.text.toString()

            openAddToContactsScreen(mPhoneNumber)
        }

        binding.btBlockNumberPU.setOnClickListener {
            val mPhoneNumber = binding.etSearch.text.toString()

            if (binding.tvBlockNumberPU.text.toString() == "Block") {
                openBlockNumberSettings(mPhoneNumber, true)
            } else {
                unBlockNumber(mPhoneNumber, true)
            }
        }

        binding.btSearch.setOnClickListener {

            MyApplication.getApplication()
                .displayInterstitialAdsWitoutIntent(
                    requireActivity(),
                    layoutInflater,
                    requireContext()
                )


            val mPhoneNumber = binding.etSearch.text.toString()

            if (mPhoneNumber.isEmpty())
                return@setOnClickListener

            if (SelectedFriend.selectedFriendNo != -1) {
                myFriendsAdapter.resetValues()
                myFriendsAdapter.notifyItemChanged(SelectedFriend.selectedFriendNo)

            }


            callLocatorFragmentUI.setContactNumber(binding.etSearch.text.toString())
            UtilFunctions.hideKeyboard(binding.etSearch)

            val myNumber = LoginData.getUserPhone(requireContext())

            if (mPhoneNumber == myNumber)
                return@setOnClickListener


            if (mPhoneNumber.length == 10) {
                if (findFriend(mPhoneNumber)) {
                    if (findInAllFriendRequests(mPhoneNumber))
                        loginAPIFunctions.findUserExistence(phoneNumber = mPhoneNumber)
                }

            }
        }

        binding.btCallPU.setOnClickListener {
            checkCallPermission(true, EnteredNumber.phoneNumber)
        }

        binding.btClear.setOnClickListener {
            callLocatorFragmentUI.clearSearch()
        }

        binding.btFriendRequest.setOnClickListener {
//            CallIntent.goToFriendRequestActivity(requireContext(), false, requireActivity())

            val intent = Intent(
                requireContext(),
                FriendRequestActivity::class.java
            )

            MyApplication.getApplication()
                .displayInterstitialAds(
                    requireActivity(),
                    intent,
                    false,
                    layoutInflater,
                    requireContext()
                )


        }

        binding.btReload.setOnClickListener {
//            callLocatorFragmentUI.startRotateAnimation()
            MyApplication.getApplication()
                .displayInterstitialAdsWitoutIntent(
                    requireActivity(),
                    layoutInflater,
                    requireContext()
                )

            getMyFriends()

        }

        binding.btCloseAddressPU.setOnClickListener {
            binding.cvAddress.visibility = View.GONE
        }
    }

    private var isCallPermissionGiven = false

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        mMap.isTrafficEnabled = true

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
            UtilFunctions.isInPermissionFlow=true
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

    private var isLocationListening = false

    private fun listenForLocation() {

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
        UtilFunctions.isInPermissionFlow=false
        // Handle the fetched location, for example, show a toast

        val phoneNumber = LoginData.getUserPhone(requireContext())
        callLocatorViewModel.updateLocation(
            phoneNumber,
            "${location.latitude}",
            "${location.longitude}"
        )

        isLocationFetched = true

        LocationData.latitude = location.latitude
        LocationData.longitude = location.longitude

        val locationLatLng = LatLng(
            location.latitude,
            location.longitude
        )

        Log.e("Test", "Address ${getAddressFromLocation(location.latitude, location.longitude)}")

        mMap.clear()

        mMap.addMarker(
            MarkerOptions().position(locationLatLng)
                .title("Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_location))
        )


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 18f))

        stopLocationUpdates()
        isLocationListening = false
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var result = ""

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressStringBuilder = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex + 1) {
                    addressStringBuilder.append(address.getAddressLine(i)).append("\n")
                }
                result = addressStringBuilder.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return result
    }

    private fun getStreetNameFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var streetName = ""

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                streetName = address.thoroughfare ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return streetName
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
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_PHONE_STATE
    )
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

//    Manifest.permission.ACCESS_BACKGROUND_LOCATION // Add this line


    private fun askPermissions() {
//        checkLocationPermission()

        checkContactPermission()
//        checkCallPermission()
    }

    private fun checkContactPermission() {
        // Check if the permission is granted
        if (arePermissionsGranted(contactPermissions)) {
            // Permission is already granted, you can proceed with contact retrieval
            checkCallPermission(false, EnteredNumber.phoneNumber)
            getContacts()
        } else {
            // Permission is not granted, request it
            requestPermission(contactPermissions, CONTACTS_PERMISSION_CODE)
        }
    }

    private fun checkCallPermission(isForCall: Boolean, phoneNumber: String) {
        // Check if CALL_PHONE and READ_PHONE_STATE permissions are granted
        if (arePermissionsGranted(callPermissions)) {
            // Both permissions are granted, proceed with the call
            isCallPermissionGiven = true
            if (isForCall)
                UtilFunctions.callNumber(requireContext(), phoneNumber)
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
//            locationUpdateViewModel.hasRequestedBackgroundLocation = true
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

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        requestPermissions(permissions, requestCode)
    }


    private fun handleContactPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with contact retrieval
            checkCallPermission(false, EnteredNumber.phoneNumber)
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

            //Old Logic
            Log.e("Test", "351")

            isCallPermissionGiven = true

            fetchLocation()
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
        Log.e("Test", "Loc Permission granted 705")

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

        getMyFriends()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    ////////////////// GET My Friends Code

    fun getMyFriends() {
        callLocatorFragmentUI.showMyFriendsPB()
        callLocatorApiFunctions.getMyFriends(LoginData.getUserPhone(requireContext()))
    }

    private var myFriendsList = ArrayList<MyFriendsDataItem?>()
    private var allFriendRequests = ArrayList<MyFriendsDataItem?>()

    private fun findFriend(phoneNumber: String): Boolean {
        for (friend in myFriendsList) {
            if (friend!!.friendNumber.equals(phoneNumber)) {
                onFriendSelected(friend)
                return false
            }
        }
        return true
    }

    private fun findInAllFriendRequests(phoneNumber: String): Boolean {
        for (friend in allFriendRequests) {
            if (friend!!.friendNumber.equals(phoneNumber)) {
                return false
            }
        }
        return true
    }

    lateinit var myFriendsAdapter: MyFriendsAdapter

    private fun myFriendsResponse(
        data: List<MyFriendsDataItem?>?,
        allFriendRequestList: List<MyFriendsDataItem?>?
    ) {
//        callLocatorFragmentUI.stopRotateAnimation()
        callLocatorFragmentUI.hideMyFriendsPB()

        if (data != null && allFriendRequestList != null) {
            myFriendsList = ArrayList(data!!)
            allFriendRequests = ArrayList(allFriendRequestList!!)

            if (myFriendsList.isNotEmpty()) {
                findNames()
                binding.noFriendsLt.visibility = View.GONE
                binding.rvFriend.visibility = View.VISIBLE

                myFriendsAdapter =
                    MyFriendsAdapter(requireContext(), myFriendsList, ::onFriendSelected)
                binding.rvFriend.apply {
                    layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    adapter = myFriendsAdapter
                }
            } else {
                binding.noFriendsLt.visibility = View.VISIBLE
                binding.rvFriend.visibility = View.GONE
            }
        } else {
            binding.noFriendsLt.visibility = View.VISIBLE
            binding.rvFriend.visibility = View.GONE
        }
    }

    private fun findNames() {
        for ((position, friendNumber) in myFriendsList.withIndex()) {
            for (contact in allContacts) {
                val dbNumber = UtilFunctions.makePhoneNumber10(friendNumber!!.friendNumber!!)
                val phoneNumber = UtilFunctions.makePhoneNumber10(contact.number)

                if (dbNumber == phoneNumber) {
//                    Log.e("Test", contact.name)
                    myFriendsList[position]?.friendName = contact.name
                    break
                }

            }
        }
    }

    private fun onFriendSelected(myFriendsDataItem: MyFriendsDataItem) {

        MyApplication.getApplication()
            .displayInterstitialAdsWitoutIntent(requireActivity(), layoutInflater, requireContext())

        if (!myFriendsDataItem.latitude.equals("EMPTY"))
            moveCameraToLocation(
                myFriendsDataItem.latitude!!.toDouble(),
                myFriendsDataItem.longitude!!.toDouble()
            )
        else
            Log.e("Test", "EMPTY ${myFriendsDataItem.friendNumber}")

//        callLocatorFragmentUI.showContactPopUp(myFriendsDataItem)
//        popUponClickListeners(myFriendsDataItem)
    }

    private fun moveCameraToLocation(lat: Double, lng: Double) {
        Log.e("Test", "$lng $lat")

        val locationLatLng = LatLng(lng, lat)

        mMap.clear()

        mMap.addMarker(
            MarkerOptions().position(locationLatLng)
                .title("Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_location))
        )


        val streetName = getStreetNameFromLocation(lng, lat)
        val address = getAddressFromLocation(lng, lat)

        addressPopUpListeners(address)

        callLocatorFragmentUI.showAddressPopUp(streetName, address)

        // Zoom level for the animation
        val zoomLevel = 18f

        // Create CameraPosition with target location and zoom level
        val cameraPosition = CameraPosition.Builder()
            .target(locationLatLng)
            .zoom(zoomLevel)
            .build()

        // Use animateCamera with CameraUpdateFactory.newCameraPosition() to animate the camera movement
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    //////ADD CONTACT CODE

    private val REQUEST_ADD_CONTACT = 1

    // Function to open the Add to Contacts screen
    private fun openAddToContactsScreen(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_INSERT)
        intent.type = ContactsContract.Contacts.CONTENT_TYPE

        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)

        startActivityForResult(intent, REQUEST_ADD_CONTACT)
    }

    // Handle result of the Add to Contacts screen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                UtilFunctions.showToast(requireContext(), "Contact Added Successfully")
                // Contact added successfully
                // Handle further operations here if needed
            } else {
                UtilFunctions.showToast(requireContext(), "Failed to add contact")

                // Contact addition canceled or failed
                // Handle accordingly
            }
        }
    }

//    private var dbHelper = BlockedContactsDBHelper(requireContext())

    private fun openBlockNumberSettings(phoneNumber: String, isCalledFromPU: Boolean) {
        val res = dbHelper.insertNumber(phoneNumber)
        if (res) {
            UtilFunctions.showToast(requireContext(), "Number Blocked Successfully")


            if (isCalledFromPU)
                binding.tvBlockNumberPU.text = "UnBlock"
            else
                binding.tvBlock.text = "UnBlock"
        }
//        Log.e("Test", "AI $res")
    }

    private fun unBlockNumber(phoneNumber: String, isCalledFromPU: Boolean) {
        val res = dbHelper.deleteNumber(phoneNumber)

        if (res) {
            UtilFunctions.showToast(requireContext(), "Number UnBlocked Successfully")

            if (isCalledFromPU)
                binding.tvBlockNumberPU.text = "Block"
            else
                binding.tvBlock.text = "Block"

        }
    }

    //PopUp OnClick Listeners
    fun popUponClickListeners(myFriendsDataItem: MyFriendsDataItem) {
        binding.btCallPU.setOnClickListener {
            checkCallPermission(true, myFriendsDataItem.friendNumber!!)
        }

        binding.btAddNumberPu.setOnClickListener {
            val mPhoneNumber = myFriendsDataItem.friendNumber
            openAddToContactsScreen(mPhoneNumber!!)
        }

        binding.btBlockNumberPU.setOnClickListener {
            val mPhoneNumber = myFriendsDataItem.friendNumber

            if (binding.tvBlockNumberPU.text.toString() == "Block") {
                openBlockNumberSettings(mPhoneNumber!!, true)
            } else {
                unBlockNumber(mPhoneNumber!!, true)
            }
        }

        binding.btClosePu.setOnClickListener {
            callLocatorFragmentUI.hideContactPopUp()
        }
    }

    fun addressPopUpListeners(address: String) {
        binding.btCopy.setOnClickListener {
            copyAddressToClipboard(address)
        }

        binding.btShare.setOnClickListener {
            shareAddress(address)
        }
    }

    fun copyAddressToClipboard(address: String) {
        val clipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Address", address)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareAddress(address: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, address)
        requireContext().startActivity(Intent.createChooser(shareIntent, "Share address"))
    }

    // Background Location Permission

//    private fun initLocationVariables()
//    {
//        val preferences = requireContext().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
//        liveSharedPreferences = LiveSharedPreferences(preferences)
//    }
//
//    private lateinit var liveSharedPreferences: LiveSharedPreferences
//
//    private val locationUpdateViewModel by lazy {
//        ViewModelProvider(this).get(LocationUpdateViewModel::class.java)
//    }
//
//    private val resolutionForResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
//        val states = activityResult.data?.let { LocationSettingsStates.fromIntent(it) }
//        when (activityResult.resultCode) {
//            BaseActivity.RESULT_OK ->
//                if (states?.isLocationUsable == true) {
//                    with(liveSharedPreferences.preferences.edit()) {
//                        putBoolean("location_services_enabled", true)
//                        apply()
//                    }
//                    locationUpdateViewModel.startLocationUpdates()
//                    Log.i(TAG, "Location services have been enabled")
//                }
//            BaseActivity.RESULT_CANCELED ->
//                showLocationServicesWarning()
//        }
//    }
//
//
//    private fun checkLocationPermissionNew():Boolean {
//        when {
//            !PermissionUtils.isForegroundLocationPermissionGranted(requireContext()) -> {
//                PermissionUtils.requestPermission(
//                    requireActivity(),
//                    arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    )
//                )
//            }
//            !PermissionUtils.isBackgroundLocationPermissionGranted(requireContext()) -> {
//                if (PermissionUtils.shouldShowRationale(requireActivity(),
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                    )
//                    || locationUpdateViewModel.hasRequestedBackgroundLocation
//                ) {
//                    showBackgroundLocationRationale()
//                } else {
//                    // This will never get reached as a rationale should always be shown for this permission
//                    requestBackgroundLocationPermission()
//                    return false
//                }
//            }
//            else -> {
//                locationUpdateViewModel.startLocationUpdates()
//                locationPermissionGranted()
//
//                return true
//            }
//        }
//
//        return false
//    }
//
//    private fun requestBackgroundLocationPermission() {
//        // Open the location permissions for the app where the user can select "Allow all the time".
//        // If we have asked previously but the user has denied it, that request will fail. So instead
//        // open the app settings where the user can navigate manually to the location permissions
//        // and modify it.
//        dialog?.dismiss()
//
//        if (!locationUpdateViewModel.hasRequestedBackgroundLocation) {
//            PermissionUtils.requestPermission(requireActivity(), arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
//            locationUpdateViewModel.hasRequestedBackgroundLocation = true
//        } else {
//            PermissionUtils.openAppSettings(requireActivity())
//        }
//    }

    /**
     * Result from requesting runtime permissions can be checked here.
     */
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode != PermissionUtils.PERMISSION_REQUEST_CODE) {
//            // We received some permission result from someone/something else.
//            return
//        }
//
//        if (grantResults.isEmpty()) {
//            // The permission request was cancelled
//            return
//        }
//
//        if (permissions.any {
//                it.contentEquals(Manifest.permission.ACCESS_FINE_LOCATION)
//                        || it.contentEquals(Manifest.permission.ACCESS_COARSE_LOCATION)
//            } && grantResults.all { it == PackageManager.PERMISSION_DENIED }) {
//            locationPermissionsDenied()
//        } else if (permissions.any { it.contentEquals(Manifest.permission.ACCESS_BACKGROUND_LOCATION) }
//            && grantResults.all { it == PackageManager.PERMISSION_DENIED }) {
//            showBackgroundLocationRationale()
//        } else {
//            // Check if any other permissions need to be requested
//            checkLocationPermission()
//        }
//    }

//    private var dialog: AlertDialog? = null

    /**
     * Ask the user to allow location updates "all the time" so that we can receive location updates
     * while the app is closed.
     */
//    private fun showBackgroundLocationRationale() {
//        if (dialog == null) {
//            dialog = MaterialAlertDialogBuilder(requireContext())
//                .setTitle(R.string.location_rationale_title)
//                .setMessage(R.string.location_rationale)
//                .setPositiveButton(getString(R.string.got_it)) { _, _ -> requestBackgroundLocationPermission() }
//                .setNegativeButton(getString(R.string.no_thanks)) { _, _ ->
//                    locationPermissionsDenied()
//                }
//                .create()
//        }
//
//        if (dialog?.isShowing == false) {
//            dialog?.show()
//        }
//    }

//    private var locationServicesWarning: Snackbar? = null

    /**
     * Ask the user to turn on Location Services so that we can receive location updates.
     */
//    private fun showLocationServicesWarning() {
//        if (locationServicesWarning == null) {
//            locationServicesWarning = Snackbar.make(
//                binding.root,
//                R.string.enable_location_services_text,
//                Snackbar.LENGTH_INDEFINITE
//            )
//                .setAction(R.string.ok) {
//                    locationUpdateViewModel.requestLocationServices(requireActivity(), resolutionForResult)
//                }
//        }
//
//        if (locationServicesWarning?.isShown == false) {
//            locationServicesWarning?.show()
//        }
//    }

    /**
     * Allows us to explicitly dismiss this warning.
     */
//    private fun dismissWarning() {
//        locationServicesWarning?.dismiss()
//    }

    /**
     * Ask the user to allow Location permissions so that we can receive location updates.
     */
//    private fun showLocationPermissionsWarning() {
//        Snackbar.make(
//            binding.root,
//            R.string.error_permissions_denied,
//            Snackbar.LENGTH_INDEFINITE
//        )
//            .setAction(R.string.settings) {
//                PermissionUtils.openAppSettings(requireActivity())
//            }
//            .show()
//    }

//    private fun locationPermissionsDenied() {
//        Log.e("Test","Location Permission Denied")
//
////        binding.mapImage.setImageDrawable(
////            AppCompatResources.getDrawable(
////                this,
////                R.drawable.ic_access_denied
////            )
////        )
//        showLocationPermissionsWarning()
//    }

    private fun locationPermissionGranted() {
        Log.e("Test", "Location Permission Granted")
//        binding.mapImage.setImageDrawable(
//            AppCompatResources.getDrawable(
//                this,
//                R.drawable.ic_map_dark
//            )
//        )

        if (!isLocationEnabled()) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

//    private fun isLocationEnabledN(): Boolean {
//
//        val locationManager =
//            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }


    companion object {
        private val TAG = MainActivity::class.simpleName
    }


}