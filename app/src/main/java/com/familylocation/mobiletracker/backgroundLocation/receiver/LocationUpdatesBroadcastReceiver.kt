/*
 * Copyright (C) 2020 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Changes:
 * 1. Check for Location Services through the LocationManager.
 * 2. Store result in Shared Preferences.
 */
package com.familylocation.mobiletracker.backgroundLocation.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.work.Data
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.backgroundLocation.apiCalls.RetrofitInstance
import com.familylocation.mobiletracker.backgroundLocation.data.LocationRepository
import com.familylocation.mobiletracker.backgroundLocation.data.db.LocationEntity
import com.familylocation.mobiletracker.backgroundLocation.utils.PermissionUtils
import com.familylocation.mobiletracker.backgroundLocation.utils.cancelAllNotifications
import com.familylocation.mobiletracker.backgroundLocation.utils.isLocationEnabled
import com.familylocation.mobiletracker.backgroundLocation.utils.showLocationUnavailableNotification
import com.familylocation.mobiletracker.callLocatorModule.apiFunctions.CallLocatorViewModel
import com.familylocation.mobiletracker.zSharedPreference.LoginData
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ibrahimsn.library.LiveSharedPreferences
import java.util.Date
import java.util.concurrent.Executors

class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    private lateinit var callLocatorViewModel: CallLocatorViewModel

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("Test", "onReceive() context:$context, intent:$intent")



        val preferences =
            context.getSharedPreferences(context.getString(R.string.pref_key), Context.MODE_PRIVATE)
        val liveSharedPreferences = LiveSharedPreferences(preferences)

        if (intent.action == ACTION_PROCESS_UPDATES) {

            // LocationAvailability.isLocationAvailable returns true if the device location is
            // known and reasonably up to date within the hints requested by the active LocationRequests.
            // Failure to determine location may result from a number of causes including disabled
            // location settings or an inability to retrieve sensor data in the device's environment.
            LocationAvailability.extractLocationAvailability(intent)?.let {
                if (!PermissionUtils.isLocationPermissionGranted(context)) {
                    Log.e(TAG, "Background location permissions have been revoked!")
                    // Note: Clearing existing notifications to prevent duplication. In production
                    // you should never just dismiss all notifications.
                    context.cancelAllNotifications()
                    context.showLocationUnavailableNotification(
                        context.getString(R.string.location_background_permission_revoked),
                        context.getString(R.string.location_rationale)
                    )
                }

                if (!it.isLocationAvailable) {
                    Log.e(TAG, "Location is currently unavailable!")
                }

                if (!context.isLocationEnabled()) {
                    Log.e(TAG, "Location Services were disabled by the user!")
                    with(liveSharedPreferences.preferences.edit()) {
                        putBoolean("location_services_enabled", false)
                        apply()
                    }
                    // Note: Clearing existing notifications to prevent duplication. In production
                    // you should never just dismiss all notifications.
                    context.cancelAllNotifications()
                    context.showLocationUnavailableNotification(
                        context.getString(R.string.location_service_disabled),
                        context.getString(R.string.enable_location_services_text)
                    )
                }
            }

            LocationResult.extractResult(intent)?.let { locationResult ->

                Log.e("Test","Loc Updates ${locationResult.lastLocation.latitude}:${locationResult.lastLocation.longitude}")

                updateLocation(context,intent,locationResult.lastLocation)

                val locations = locationResult.locations.map {
                    LocationEntity(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        foreground = isAppInForeground(context),
                        recordedAt = Date(it.time)
                    )
                }
                if (locations.isNotEmpty()) {
                    LocationRepository.getInstance(context, Executors.newSingleThreadExecutor())
                        .addLocations(locations)

                }
            }
        }
    }

    // Note: This function's implementation is only for debugging purposes. If you are going to do
    // this in a production app, you should instead track the state of all your activities in a
    // process via android.app.Application.ActivityLifecycleCallbacks's
    // unregisterActivityLifecycleCallbacks(). For more information, check out the link:
    // https://developer.android.com/reference/android/app/Application.html#unregisterActivityLifecycleCallbacks(android.app.Application.ActivityLifecycleCallbacks
    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        appProcesses.forEach { appProcess ->
            if (appProcess.importance ==
                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == context.packageName
            ) {
                return true
            }
        }
        return false
    }

    fun updateLocation(context: Context,intent: Intent,location: Location)
    {
        try {

            val phoneNumber = LoginData.getUserPhone(context)
            Log.e("Test", "API Calling $phoneNumber")

//            val lat = intent.getStringExtra("lat") ?: return
//            val lng = intent.getStringExtra("lng") ?: return

            Log.e("Test", "API Calling 2")


            val data = Data.Builder()
                .putString("phoneNumber", phoneNumber)
                .putString("lat", location.latitude.toString())
                .putString("lng", location.longitude.toString())
                .build()

            Log.e("Test", "API Calling 3")

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitInstance.api.updateLocation(
                        phoneNumber,location.latitude.toString(),location.longitude.toString()
                    )

                    if (response.code==200) {

                        Log.e("LocationUpdate", "Success ")
                    } else {
                        Log.e("LocationUpdate", "Failed")
                    }
                } catch (e: Exception) {
                    Log.e("LocationUpdate", "Exception: ${e.message}")
                }
            }

//            val workRequest = OneTimeWorkRequestBuilder<LocationUpdateWorker>()
//                .setInputData(data)
//                .build()

            Log.e("Test", "Calling API...")

//            WorkManager.getInstance(context).enqueue(workRequest)

        }catch (exception: Exception)
        {
            Log.e("Test","ServiceError ${exception.message}")
        }
    }

    fun updateLocationNew(context: Context,location : Location)
    {
        val phoneNumber = LoginData.getUserPhone(context)
        callLocatorViewModel.updateLocation(
            phoneNumber,
            "${location.latitude}",
            "${location.longitude}"
        )
    }

    companion object {
        private val TAG = LocationUpdatesBroadcastReceiver::class.simpleName

        const val ACTION_PROCESS_UPDATES =
            "com.familylocation.mobiletracker.action.PROCESS_UPDATES"
    }
}