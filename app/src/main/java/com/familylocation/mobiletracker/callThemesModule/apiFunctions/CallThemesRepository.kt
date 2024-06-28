package com.familylocation.mobiletracker.callThemesModule.apiFunctions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.familylocation.mobiletracker.callThemesModule.modalClass.GetThemesResponse
import com.familylocation.mobiletracker.zAPIEndPoints.ApiHelper
import javax.inject.Inject


class CallThemesRepository @Inject constructor(
    private val apiHelper: ApiHelper
) {
    private var getThemesResponse = GetThemesResponse()

    suspend fun getThemes(): GetThemesResponse {
        try {
            withContext(Dispatchers.IO) {
                getThemesResponse = apiHelper.getThemes()
            }
        } catch (_: Exception) {
        }
        return getThemesResponse
    }

}