package com.familylocation.mobiletracker.backgroundLocation.viewmodels

import com.familylocation.mobiletracker.backgroundLocation.data.LocationRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.util.concurrent.Executors




class LocationHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )
}