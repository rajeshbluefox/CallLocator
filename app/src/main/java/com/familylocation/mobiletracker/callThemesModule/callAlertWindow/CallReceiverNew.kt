package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions

class CallReceiverNew : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        UtilFunctions.showToast(context!!, "Received")
//        newLogic1(context, intent)

        Log.e("Test","Call REceived")
        if (context != null && intent != null) {
            Log.e("Test","Call REceived 1")

            newLogic2(context, intent)
        }
    }

    private fun newLogic2(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val serviceIntent = Intent(context, CallService::class.java).apply {
                putExtra(
                    TelephonyManager.EXTRA_STATE,
                    intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                )
                putExtra(
                    TelephonyManager.EXTRA_INCOMING_NUMBER,
                    intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                )
            }
            context.startService(serviceIntent)
        }
    }

}