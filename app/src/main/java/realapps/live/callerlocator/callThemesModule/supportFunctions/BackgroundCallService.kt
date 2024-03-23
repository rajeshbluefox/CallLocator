package realapps.live.callerlocator.callThemesModule.supportFunctions

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callThemesModule.IncomingCallActivity
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zDatabase.BlockedContactsDBHelper

class BackgroundCallService : Service() {

    private var callReceiver: CallReceiver? = null

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
        callReceiver = CallReceiver()
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

    inner class CallReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

                //Logic to block Call
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if (incomingNumber != null) {
                    Log.e("Testing", "number $incomingNumber")

                    val blockedContactsDB = BlockedContactsDBHelper(context!!)
                    val mIncomingNumber = UtilFunctions.makePhoneNumber10(incomingNumber!!)
                    val blockedStatus = blockedContactsDB.findNumber(mIncomingNumber)
                    if (blockedStatus) {
                        val telecomManager = getSystemService(TelecomManager::class.java)
                        telecomManager?.endCall()

                        UtilFunctions.showToast(context!!, "Number Blocked")
                    } else {

                        when (state) {
                            TelephonyManager.EXTRA_STATE_RINGING -> {
                                // Incoming call detected
                                val incomingNumber =
                                    intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                                if (incomingNumber != null) {
                                    Log.e("Testing", "number $incomingNumber")
                                    showIncomingCallScreen(incomingNumber)

//                            val blockedContactsDB = BlockedContactsDBHelper(context!!)
//                            val mIncomingNumber = UtilFunctions.makePhoneNumber10(incomingNumber!!)
//                            val blockedStatus = blockedContactsDB.findNumber(mIncomingNumber)

//                            if (blockedStatus) {
//                                val telecomManager = getSystemService(TelecomManager::class.java)
//                                telecomManager?.endCall()
//
//                                UtilFunctions.showToast(context!!, "Number Blocked")
//                            } else {
////                                if (!incomingNumber.isNullOrEmpty()) {
//                                showIncomingCallScreen(incomingNumber)
////                                }
//                            }

                                }


                            }

                            TelephonyManager.EXTRA_STATE_IDLE -> {
                                // Call ended
                                val intent = Intent("ACTION_CALL_ENDED")
                                context?.sendBroadcast(intent)
                                hideIncomingCallScreen()
                            }


                        }
                    }

                }
            }
        }

        private fun disconnectCall(context: Context) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val telephonyClass = Class.forName(telephonyManager.javaClass.name)
            val method = telephonyClass.getDeclaredMethod("getITelephony")
            method.isAccessible = true
            val telephonyInterface = method.invoke(telephonyManager)
            val telephonyInterfaceClass = Class.forName(telephonyInterface.javaClass.name)
            val endCallMethod = telephonyInterfaceClass.getDeclaredMethod("endCall")
            endCallMethod.invoke(telephonyInterface)
        }
    }

    private fun showIncomingCallScreen(incomingNumber: String) {
//        UtilFunctions.showToast(this, "Call Coming")

        try {
            val incomingCallIntent = Intent(this, IncomingCallActivity::class.java)
            incomingCallIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

//            incomingCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            incomingCallIntent.putExtra("incoming_number", incomingNumber)
            startActivity(incomingCallIntent)
        } catch (e: Exception) {
            Log.e("Test", e.message.toString())
        }

    }

    private fun hideIncomingCallScreen() {
        // Close or hide the incoming call screen


    }

    companion object {
        private const val NOTIFICATION_ID = 1

    }


}
