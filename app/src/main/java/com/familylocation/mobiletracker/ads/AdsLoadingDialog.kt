package com.familylocation.mobiletracker.ads

import android.app.AlertDialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import com.familylocation.mobiletracker.R

class AdsLoadingDialog(
    layoutInflater: LayoutInflater,
    context: Context
) {

    private val mLayoutInflater: LayoutInflater
    private val mContext: Context

    private lateinit var dialog: AlertDialog.Builder
    private lateinit var messageBoxInstance: AlertDialog


    init {
        mLayoutInflater = layoutInflater
        mContext = context

        initViews()
    }


    fun initViews() {
        val view = mLayoutInflater.inflate(R.layout.alert_ad_loading, null)
        dialog = AlertDialog.Builder(mContext, R.style.CurvedDialog).setView(view)
        dialog.setCancelable(false)
        messageBoxInstance = dialog.create()

        // Set the width of the dialog to 50% of the device screen width
        val window = messageBoxInstance.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            layoutParams.width = (mContext.resources.displayMetrics.widthPixels * 0.5).toInt()
            window.attributes = layoutParams
        }
    }

    fun openAdsLoading() {
        messageBoxInstance.show()
    }

    fun closeAdsLoading()
    {
        messageBoxInstance.dismiss()
    }


    fun make50()
    {
        // Get the display metrics
        val displayMetrics = DisplayMetrics()
        (mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels

        // Calculate the dialog window dimensions
        val dialogWindowWidth = (displayWidth * 0.5f).toInt()
        val dialogWindowHeight = (displayHeight * 0.5f).toInt()

        // Set the dialog window attributes
        val layoutParams = WindowManager.LayoutParams().apply {
            copyFrom(messageBoxInstance?.window?.attributes)
            width = dialogWindowWidth
            height = dialogWindowHeight
        }
        messageBoxInstance?.window?.attributes = layoutParams
    }

}