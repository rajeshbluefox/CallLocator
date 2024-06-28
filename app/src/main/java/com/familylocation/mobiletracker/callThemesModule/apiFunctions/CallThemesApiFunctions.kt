package com.familylocation.mobiletracker.callThemesModule.apiFunctions

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.familylocation.mobiletracker.callThemesModule.modalClass.ThemesData


class CallThemesApiFunctions(
    val context: Context,
    val activity: Activity,
    private val lifeCycleOwner: LifecycleOwner,
    private var callThemesViewModel: CallThemesViewModel,
    private val getThemesResponse: (data: List<ThemesData>) -> Unit,
)
{
    fun getThemes()
    {
        callThemesViewModel.resetGetThemes()
        callThemesViewModel.getThemes()
        getThemesObserver()
    }

    private fun getThemesObserver()
    {
        callThemesViewModel.getThemesResponse().observe(lifeCycleOwner){
            if(it.status==200)
            {
                Log.e("Test","Size ${it.data?.size}")

                if(it.data!!.isNotEmpty())
                {
                    getThemesResponse.invoke(it.data as List<ThemesData>)
                }
            }
        }
    }
}