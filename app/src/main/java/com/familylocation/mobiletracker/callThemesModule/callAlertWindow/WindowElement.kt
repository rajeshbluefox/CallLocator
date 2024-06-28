package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.view.View
import android.view.WindowManager

object WindowElement {
    var isInitialised = false
//    var window: WindowNew? = null

    var isOpened = false

    lateinit var mView: View

    lateinit var mWindowManager: WindowManager
    fun resetValues()
    {
        isInitialised = false
        isOpened = false
    }
}