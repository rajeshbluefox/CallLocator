package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.content.ContentUris
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.telecom.TelecomManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callSettingsModule.supportFunctions.FlashlightManager
import com.familylocation.mobiletracker.setThemeModule.supportFunctions.ContactDatabaseHelper
import com.familylocation.mobiletracker.zCommonFuntions.Contact
import com.familylocation.mobiletracker.zCommonFuntions.ContactManager
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.SettingsData
import java.io.File
import java.io.FileInputStream
import java.util.Locale
import java.util.Objects
import kotlin.math.sqrt

class WindowNewAnnouner(private val context: Context) : TextToSpeech.OnInitListener {

    private var mView: View
    private var mParams: WindowManager.LayoutParams
    private var mWindowManager: WindowManager
    private var layoutInflater: LayoutInflater

    private var tvCallerName: TextView
    private var tvIncomingNumber: TextView

    private var declineBt: LottieAnimationView
    private var acceptBt: LottieAnimationView

    private var callerTheme: LottieAnimationView

    private var dbHelper = ContactDatabaseHelper(context)


    private var tts: TextToSpeech? = null
    init {

        tts = TextToSpeech(context, this)

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = layoutInflater.inflate(R.layout.activity_incoming_call, null)

        tvCallerName = mView.findViewById(R.id.userName)
        tvIncomingNumber = mView.findViewById(R.id.incomingNumberTextView)


        callerTheme = mView.findViewById(R.id.callerTheme)

        declineBt = mView.findViewById(R.id.btDecline)
        acceptBt = mView.findViewById(R.id.btAccept)

        acceptBt.setOnClickListener {
            val tm = context
                .getSystemService(AppCompatActivity.TELECOM_SERVICE) as TelecomManager
            tm.acceptRingingCall()
//            UtilFunctions.showToast(context, "Accepting")
            close()
        }

        declineBt.setOnClickListener {
            val telecomManager = context.getSystemService(TelecomManager::class.java)
            telecomManager?.endCall()
//            UtilFunctions.showToast(context, "Rejected")
            close()
        }


        // Set the layout parameters to match the parent's size
        val parentLayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mView.layoutParams = parentLayoutParams


        mParams =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,

                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
//            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
        //            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  original

//        mParams.gravity = Gravity.TOP or Gravity.START
        mParams.gravity = Gravity.TOP or Gravity.START //original
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager





    }


    fun open(incomingNumber: String) {
        try {
            if (mView.windowToken == null) {
                if (mView.parent == null) {

                    WindowElement.isOpened = true
                    WindowElement.isInitialised = true

                    setDetails(incomingNumber)
//                    registerCallEndedReceiver()
                    onOpened()
                    mWindowManager.addView(mView, mParams)

                    WindowElement.mView = mView
                    WindowElement.mWindowManager = mWindowManager
                }
            }
        } catch (e: Exception) {
            Log.e("Error1", e.toString())
        }
    }

    fun close() {
        try {

//            mWindowManager.removeView(mView)

            WindowElement.mWindowManager.removeView(WindowElement.mView)

            Log.e("Test", "Closing Window")
            WindowElement.isOpened = false
            WindowElement.isInitialised = false

//            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            mView.invalidate()

            onClosed()
            stopFlashLight()
            stopAnnouncment()
//            context.unregisterReceiver(callEndedReceiver)

            (mView.parent as? ViewGroup)?.removeAllViews()
        } catch (e: Exception) {
            Log.e("Test", "closeError $e")
        }
    }


    private fun setDetails(incomingNumber: String) {

        tvIncomingNumber.text = incomingNumber

        findName(incomingNumber)
        findThemeandSet(incomingNumber)
        checkCallAnnouncment(incomingNumber)
        checkCallFlashLight()
        initSensor()
    }

    private fun findName(incomingNumber: String) {
        val callerName = getCallerName(incomingNumber)
        tvCallerName.text = callerName
    }

    private var allContacts: ArrayList<Contact> = ArrayList()

    private fun getCallerName(incomingNumber: String): String {
        Log.e("Test", "Reading contacts")

        val contactManager = ContactManager(context)
        val contacts = contactManager.getContacts()

        allContacts = contacts as ArrayList<Contact>

        val incomingNumber10 = UtilFunctions.makePhoneNumber10(incomingNumber)

        for (contact in allContacts) {
            var number = UtilFunctions.normalizePhoneNumber(contact.number)

            number = UtilFunctions.makePhoneNumber10(number)
//            Log.e("Test","$number == $incomingNumber10")
            if (number == incomingNumber10)
                return contact.name
        }
        return "UnKnown Number"

    }

    private fun findThemeandSet(incomingNumber: String) {

        val locPhoneNumber: String = UtilFunctions.makePhoneNumber10(incomingNumber)

        Log.e("Test", "Searching $locPhoneNumber")

        val theme = dbHelper.getThemeForPhoneNumber(locPhoneNumber)

        Log.e("Test", "Found Theme $theme")

        val defaultThemeId = SettingsData.getDefaultTheme(context)
        Log.e("Test", "L1 $defaultThemeId")

        when (theme) {
            -1, 0 -> {
                if (defaultThemeId == "EMPTY")
                    callerTheme.setAnimation(R.raw.call_theme_1)
                else
//                    chkAndCall(defaultThemeId)
                    loadAnimationFromJsonFile(defaultThemeId)
            }

            else -> {
                val themeId = "call_theme_$theme.json"
                chkAndCall(themeId)
                loadAnimationFromJsonFile(themeId)

            }

        }
    }

    private fun chkAndCall(fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            loadAnimationFromJsonFileNew(fileName)
        } else {
            loadAnimationFromJsonFile(fileName)
        }
    }

    private fun loadAnimationFromJsonFile(fileName: String) {
        Log.e("Test", "Loading... $fileName")

//        Environment.getExternalStorageDirectory()

            .toString()
        val file = File(
            context.getExternalFilesDir(null)
                .toString() + "/Download/CallApp/$fileName"
        )

        if (!file.exists()) {
            Log.e("Test", "No Such file found")
            return
        }

        try {
            val inputStream = FileInputStream(file)

            val composition =
                LottieCompositionFactory.fromJsonInputStreamSync(inputStream, fileName).value
            inputStream.close()

            // Set the composition to LottieAnimationView
            callerTheme.setComposition(composition!!)
            callerTheme.playAnimation()
        } catch (e: Exception) {

            Log.e("Test", "Error Loading file ${e.message}")

            UtilFunctions.showToast(context, "Error loading animation")
        }
    }

    private fun loadAnimationFromJsonFileNew(fileName: String) {
        val downloadsUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.DownloadColumns.DISPLAY_NAME,
            MediaStore.DownloadColumns.RELATIVE_PATH
        )
        val selection = "${MediaStore.DownloadColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        val cursor =
            context.contentResolver.query(downloadsUri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.DownloadColumns._ID)
            val id = cursor.getLong(idColumn)
            val uri = ContentUris.withAppendedId(downloadsUri, id)

            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val result = LottieCompositionFactory.fromJsonInputStream(inputStream, fileName)
                    result.addListener { composition ->
                        callerTheme.setComposition(composition)
                        callerTheme.playAnimation()
                    }
                    result.addFailureListener { error ->
                        Log.e("Test", "Error Loading file", error)
                        UtilFunctions.showToast(context, "Error loading animation")
                    }
                    inputStream.close()
                } else {
                    Log.e("Test", "File not found")
                }
            } catch (e: Exception) {
                Log.e("Test", "Error Loading file", e)
                UtilFunctions.showToast(context, "Error loading animation")
            }
        } else {
            Log.e("Test", "No such file found: $fileName")
        }
        cursor?.close()
    }


    private fun checkCallFlashLight() {
        val status = SettingsData.getCallFlashLightStatus(context)

        if (status) {
//            UtilFunctions.showToast(context, "Flash Light Started")
            isFlashLightOn = true
            FlashlightManager.toggleFlashlightBlink(context, 300)
        }
    }

    fun stopFlashLight() {
        val status = SettingsData.getCallFlashLightStatus(context)

        if (status) {
            isFlashLightOn = false
            FlashlightManager.stopFlashlightBlink(context)
        }
    }

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private var isFlashLightOn = false

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

    // Announcer Code//////////////////////

    private fun stopAnnouncment() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    private var mCallerName = "UnKnown"
    private var mCallerFrequency = 1

    private fun checkCallAnnouncment(incomingNumber: String) {

        val callAnnouncmentStatus = SettingsData.getCallAnnounceStatus(context)

        if (callAnnouncmentStatus) {
            val callerName = getCallerName(incomingNumber)
            val callAnnFreq = SettingsData.getCallAnnouncmentFrequency(context)

            mCallerName = callerName
            mCallerFrequency = callAnnFreq

//            announceCaller(callerName, callAnnFreq)
        }
    }

    private fun announceCaller(callerName: String?, frequency: Int) {

        try {

            Log.e("Test", "announceCaller")

            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

            // Lower the ringtone volume
//            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, 4, 0)

            val tts = TextToSpeech(context) {
                if (it == TextToSpeech.SUCCESS) {
                    Log.e("Test", "Speech Success $frequency")

                    tts?.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_ALARM).build()
                    )
                    for (i in 0 until frequency) {
                        tts?.speak(
                            "Incoming call from $callerName",
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                        )
                    }
                } else {
                    Log.e("Test", "Speech Failed")
                }
            }

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Utterance started
                }

                override fun onDone(utteranceId: String?) {
                    // Utterance completed
                    // Restore the original ringtone volume
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, originalVolume, 0)
                    tts.shutdown()
                }

                override fun onError(utteranceId: String?) {
                    // Error during utterance
                }
            })
        } catch (e: Exception) {
            UtilFunctions.showToast(context, "Call Announcer Not Working")
        }
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language if needed
            tts?.language = Locale.getDefault()

            val callAnnouncmentStatus = SettingsData.getCallAnnounceStatus(context)

            if(callAnnouncmentStatus) {
                announceCaller(mCallerName, mCallerFrequency)
            }
            // Example: Announce a message
//            announceMessage("Hello, this is a test.")
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }


}