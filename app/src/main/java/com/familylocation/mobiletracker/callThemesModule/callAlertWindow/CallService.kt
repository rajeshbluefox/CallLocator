package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log

class CallService : Service() {

    private var windowNewAnnouncer: WindowNewAnnouner? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)


        Log.e("Test","Call REceived 2 $incomingNumber")


        if (state == TelephonyManager.EXTRA_STATE_RINGING && incomingNumber != null) {
            if (windowNewAnnouncer == null) {
                windowNewAnnouncer = WindowNewAnnouner(this)
            }
            Log.e("Test","Call REceived 3")

            windowNewAnnouncer?.open(incomingNumber)
        } else if (state == TelephonyManager.EXTRA_STATE_IDLE || state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            windowNewAnnouncer?.close()
            stopSelf() // Stop the service when the call ends
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
