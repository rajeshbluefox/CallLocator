package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import com.familylocation.mobiletracker.callSettingsModule.supportFunctions.FlashlightManager
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.SettingsData
import java.util.Objects
import kotlin.math.sqrt

class CallService : Service() {

    private var windowNewAnnouncer: WindowNewAnnouner? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        UtilFunctions.showToast(context, "Call Received 1")

        Log.e("Test", "Call REceived 2 $incomingNumber")
        context = this
        initSensor()

//        if (state == TelephonyManager.EXTRA_STATE_RINGING && incomingNumber != null) {

        if (state == TelephonyManager.EXTRA_STATE_RINGING) {

            checkCallFlashLight()
            onOpened()

            //TODO Uncomment For CallThemes -- Start
//            if (windowNewAnnouncer == null) {
//                windowNewAnnouncer = WindowNewAnnouner(this)
//            }
//            windowNewAnnouncer?.open(incomingNumber)
            //TODO --END


        } else if (state == TelephonyManager.EXTRA_STATE_IDLE || state == TelephonyManager.EXTRA_STATE_OFFHOOK) {

            stopFlashLight()
            onClosed()
//            windowNewAnnouncer?.close()
//            stopSelf() // Stop the service when the call ends
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun checkCallFlashLight() {
        val status = SettingsData.getCallFlashLightStatus(context)

        if (status) {
//            UtilFunctions.showToast(context, "Flash Light Started")
            FlashlightManager.toggleFlashlightBlink(context, 300)
        }
    }

    fun stopFlashLight() {
        val status = SettingsData.getCallFlashLightStatus(context)

        if (status) {
            FlashlightManager.stopFlashlightBlink(context)
        }
    }

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private var isFlashLightOn = false

    private lateinit var context: Context

    private fun initSensor() {
        // Getting the Sensor Manager instance
        if (SettingsData.getShakeStatus(context)) {

            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

            Objects.requireNonNull(sensorManager)!!
                .registerListener(
                    sensorListener,
                    sensorManager!!
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
                )

            acceleration = 10f
            currentAcceleration = SensorManager.GRAVITY_EARTH
            lastAcceleration = SensorManager.GRAVITY_EARTH
        }
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 12) {
                if (isFlashLightOn)
                    stopFlashLight()
//                Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    private fun onOpened() {
        if (SettingsData.getShakeStatus(context)) {
            sensorManager?.registerListener(
                sensorListener, sensorManager!!.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER
                ), SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun onClosed() {
        if (SettingsData.getShakeStatus(context)) {
            sensorManager!!.unregisterListener(sensorListener)
        }
    }
}
