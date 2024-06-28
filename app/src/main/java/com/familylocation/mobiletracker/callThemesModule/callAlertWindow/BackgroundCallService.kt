package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.familylocation.mobiletracker.R


class BackgroundCallService : Service() {

    private var callReceiver: CallReceiverNew? = null

    override fun onCreate() {
        super.onCreate()

        registerCallReceiver()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Service will continue running until explicitly stopped
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun registerCallReceiver() {
        callReceiver = CallReceiverNew()
        val intentFilter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(callReceiver, intentFilter)
    }

    private fun startForegroundService() {
//        val notification = createNotification()
//        startForeground(NOTIFICATION_ID, notification)
//        UtilFunctions.showToast(this,"Starting FGS")

        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("Test", "55")

            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            Log.e("Test", "63")
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(): Notification {
        val channelId = "BackgroundCallServiceChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Background Call Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call Screen Theme Enabled")
            .setContentText("You are using customised call screen")
            .setSmallIcon(R.drawable.baseline_my_location_36)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(callReceiver)
    }

    companion object {
        private const val NOTIFICATION_ID = 1

    }

}
