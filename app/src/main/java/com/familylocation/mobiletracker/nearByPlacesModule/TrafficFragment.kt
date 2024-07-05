package com.familylocation.mobiletracker.nearByPlacesModule

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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callLocatorModule.modalClass.LocationData
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.PermissionResultListener
import com.familylocation.mobiletracker.databinding.FragmentTrafficBinding
import com.familylocation.mobiletracker.zSharedPreference.LoginData


class TrafficFragment : Fragment(), OnMapReadyCallback, PermissionResultListener {

    private lateinit var binding: FragmentTrafficBinding

    private lateinit var mMap: GoogleMap

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val coroutineScope =
        CoroutineScope(Dispatchers.Main) // Dispatchers.Main represents the main (UI) thread


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_traffic, container, false)

        return binding.root
        // Inflate the layout for this fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLocationPermission()

        coroutineScope.launch {
            setMapView()
        }

        binding.btLocation.setOnClickListener {
            getLocation()
        }
    }

    private fun getLocation() {
        if (checkLocationPermission()) {
            fetchLocation()
        }

    }

    private fun setMapView() {
        // Create a SupportMapFragment
        val mapFragment = SupportMapFragment.newInstance()

        // Add the SupportMapFragment to the container
        childFragmentManager.beginTransaction()
            .replace(R.id.mapTraffic, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        mMap.isTrafficEnabled=true

    }

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val permissionId = 1003

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

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        requestPermissions(permissions, requestCode)
    }

    override fun onPermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionId -> handleLocationPermissionResult(grantResults)
        }
    }

    private fun handleLocationPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            fetchLocation()
//            checkContactPermission()
        }
        // Handle the case where location permission is denied if needed
    }

    private lateinit var locationCallback: LocationCallback

    private fun isLocationEnabled(): Boolean {

        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
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

    private var isLocationFetched = false

    private fun onLocationFetched(location: Location) {
        // Handle the fetched location, for example, show a toast

        val phoneNumber = LoginData.getUserPhone(requireContext())
//        callLocatorViewModel.updateLocation(
//            phoneNumber,
//            "${location.latitude}",
//            "${location.longitude}"
//        )

        isLocationFetched = true

        LocationData.latitude = location.latitude
        LocationData.longitude = location.longitude

        val locationLatLng = LatLng(
            location.latitude,
            location.longitude
        )

//        Log.e("Test", "Address ${getAddressFromLocation(location.latitude, location.longitude)}")

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

}