package realapps.live.callerlocator.callThemesModule.supportFunctions

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
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

    private var windowManager: WindowManager? = null

    @SuppressLint("StaticFieldLeak")
    private var windowLayout: ViewGroup? = null

    private val WINDOW_WIDTH_RATIO = 0.8f
    private var params: WindowManager.LayoutParams? = null
    private var x = 0f
    private var y = 0f

    lateinit var window : Window

    inner class CallReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {


            if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {

                UtilFunctions.showToast(context!!, "CC 1")

                if(!WindowElement.isInitialised)
                {
                    WindowElement.isInitialised=true
                    window = Window(context!!)
//                    WindowElement.window = window
                }

                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

                if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                    //Logic to block Call
                    val incomingNumber =
                        intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    UtilFunctions.showToast(context!!, "Call Coming")
                    if (incomingNumber != null) {
//                    showWindow(context, incomingNumber)
                        UtilFunctions.showToast(context!!, "Number Detected")

                        window.open(incomingNumber)
                    }else{
                        UtilFunctions.showToast(context!!, "Number is not detected Detected")
                    }
//                incomingNumber != null


                    if (incomingNumber == "999") {
                        UtilFunctions.showToast(context!!, "IC 1")
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
                }else{
                    window.close()
                    Log.e("Test","Call Ended Receiver Else")
                }

            }else if(intent?.action == "ACTION_CALL_ENDED")
            {
                Log.e("Test","Call Ended Receiver")
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
//            incomingCallIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            incomingCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            incomingCallIntent.putExtra("incoming_number", incomingNumber)
            startActivity(incomingCallIntent)
            Log.e("Test","Opened activity")

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

    ////////////////////////////////////////////////////////////////////////////


    private fun showWindow(context: Context, phone: String) {

        Log.e("Test","SShowing Window")
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowLayout = View.inflate(context, R.layout.alert_call_window, null) as ViewGroup
        getLayoutParams()
        setOnTouchListener()

        val numberTextView = windowLayout?.findViewById<TextView>(R.id.incomingNumberTextView)
        numberTextView?.text = phone
        val cancelButton = windowLayout?.findViewById<Button>(R.id.btDecline)
        cancelButton?.setOnClickListener { closeWindow() }

        windowManager?.addView(windowLayout, params)
    }

    private fun getLayoutParams() {
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getWindowsTypeParameter(),
            (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL),
            PixelFormat.TRANSLUCENT
        )
        params!!.gravity = Gravity.CENTER
        params!!.format = 1
        params!!.width = getWindowWidth()
    }

    private fun getWindowsTypeParameter(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun getWindowWidth(): Int {
        val metrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(metrics)
        return (WINDOW_WIDTH_RATIO * metrics.widthPixels.toDouble()).toInt()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener() {
        windowLayout?.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX
                    y = event.rawY
                }
                MotionEvent.ACTION_MOVE -> updateWindowLayoutParams(event)
                MotionEvent.ACTION_UP -> view.performClick()
                else -> {
                }
            }
            false
        }
    }

    private fun updateWindowLayoutParams(event: MotionEvent) {
        params!!.x = (params!!.x - (x - event.rawX)).toInt()
        params!!.y = (params!!.y - (y - event.rawY)).toInt()
        windowManager?.updateViewLayout(windowLayout, params)
        x = event.rawX
        y = event.rawY
    }

    private fun closeWindow() {
        Log.e("Test","Closing Window")

        if (windowLayout != null) {
            windowManager?.removeView(windowLayout)
            windowLayout = null
        }
    }


}
