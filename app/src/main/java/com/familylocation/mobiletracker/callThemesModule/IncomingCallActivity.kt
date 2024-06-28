package com.familylocation.mobiletracker.callThemesModule

import android.content.Context
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.telecom.TelecomManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieCompositionFactory
import dagger.hilt.android.AndroidEntryPoint
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callSettingsModule.supportFunctions.FlashlightManager
import com.familylocation.mobiletracker.callThemesModule.supportFunctions.CallEndedReceiver
import com.familylocation.mobiletracker.databinding.ActivityIncomingCallBinding
import com.familylocation.mobiletracker.setThemeModule.supportFunctions.ContactDatabaseHelper
import com.familylocation.mobiletracker.zCommonFuntions.Contact
import com.familylocation.mobiletracker.zCommonFuntions.ContactManager
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.SettingsData
import java.io.File
import java.io.FileInputStream
import java.util.Locale
import java.util.Objects


@AndroidEntryPoint
class IncomingCallActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityIncomingCallBinding

    private lateinit var incomingNumber: String

    private var dbHelper = ContactDatabaseHelper(this)

    private lateinit var callEndedReceiver: CallEndedReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)

        UtilFunctions.showToast(this,"IC 2")

//        StatusBarUtils.transparentStatusBar(this)
//        StatusBarUtils.setTopPadding(resources,binding.callerTheme)
//
        callEndedReceiver = CallEndedReceiver()
        callEndedReceiver.initCallBack(::onCallEnded)
        val filter = IntentFilter("ACTION_CALL_ENDED")
        registerReceiver(callEndedReceiver, filter, RECEIVER_EXPORTED)
//
        tts = TextToSpeech(this, this)
//
//        setDetails()
//        onClickListeners()
    }

    private fun onCallEnded() {
        finish()
        stopFlashLight()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        unregisterReceiver(callEndedReceiver)
    }

    private fun onClickListeners() {
        binding.btAccept.setOnClickListener {

            val tm = this
                .getSystemService(TELECOM_SERVICE) as TelecomManager
            tm.acceptRingingCall()
            UtilFunctions.showToast(this, "Accepting")
//            finish()
            closeApplication()
        }

        binding.btDecline.setOnClickListener {

            val telecomManager = getSystemService(TelecomManager::class.java)
            telecomManager?.endCall()
            UtilFunctions.showToast(this, "Rejected")
            closeApplication()
        }
    }

    private fun closeApplication() {
        stopFlashLight()
        finish()
    }

    private fun setDetails() {

        incomingNumber = intent.getStringExtra("incoming_number") ?: ""

        binding.incomingNumberTextView.text = incomingNumber

        findName(incomingNumber)
        findThemeandSet(incomingNumber)
//        checkCallAnnouncment(incomingNumber)
//        checkCallFlashLight()
//        initSensor()
    }

    private fun findName(incomingNumber: String) {
        val callerName = getCallerName(incomingNumber)
        binding.userName.text = callerName
    }

    private fun checkCallAnnouncment(incomingNumber: String) {

        val callAnnouncmentStatus = SettingsData.getCallAnnounceStatus(this)

        if (callAnnouncmentStatus) {
            val callerName = getCallerName(incomingNumber)
            val callAnnFreq = SettingsData.getCallAnnouncmentFrequency(this)
            announceCaller(callerName, callAnnFreq)
        }
    }

    private var allContacts: ArrayList<Contact> = ArrayList()

    private fun getCallerName(incomingNumber: String): String {
        Log.e("Test", "Reading contacts")

        val contactManager = ContactManager(this)
        val contacts = contactManager.getContacts()

        allContacts = contacts as ArrayList<Contact>

        val incomingNumber10 =UtilFunctions.makePhoneNumber10(incomingNumber)

        for (contact in allContacts) {
            var number = UtilFunctions.normalizePhoneNumber(contact.number)

            number=UtilFunctions.makePhoneNumber10(number)
//            Log.e("Test","$number == $incomingNumber10")
            if (number == incomingNumber10)
                return contact.name
        }

        return "UnKnown Number"

    }

    private fun findThemeandSet(incomingNumber: String) {

        var locPhoneNumber = UtilFunctions.normalizePhoneNumber(incomingNumber)
        locPhoneNumber = UtilFunctions.makePhoneNumber10(incomingNumber)

        Log.e("Test","Searching $locPhoneNumber")

        val theme = dbHelper.getThemeForPhoneNumber(locPhoneNumber)

//        if (theme == 0) {
//            val trimmedNumber =
//                if (incomingNumber.startsWith("+91")) incomingNumber.substring(3) else incomingNumber
//            theme = dbHelper.getThemeForPhoneNumber(trimmedNumber)
//        }


        Log.e("Test", "Found Theme $theme")

        val defaultThemeId = SettingsData.getDefaultTheme(this)
        Log.e("Test","L1 $defaultThemeId")


//        if(defaultThemeId=="EMPTY")
//            theme=0

        when (theme) {
            -1,0 -> {
                if (defaultThemeId == "EMPTY")
                    binding.callerTheme.setAnimation(R.raw.call_theme_1)
                else
                    loadAnimationFromJsonFile(defaultThemeId)
            }
            else -> {
                val themeId ="call_theme_$theme.json"
                loadAnimationFromJsonFile(themeId)

            }




        }
    }

    private fun checkCallFlashLight() {
        val status = SettingsData.getCallFlashLightStatus(this)

        if (status) {
            isFlashLightOn = true
            FlashlightManager.toggleFlashlightBlink(this, 300)
        }
    }

    fun stopFlashLight() {
        val status = SettingsData.getCallFlashLightStatus(this)

        if (status) {
            isFlashLightOn = false
            FlashlightManager.stopFlashlightBlink(this)
        }
    }

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private var isFlashLightOn = false

    private fun initSensor() {
        // Getting the Sensor Manager instance
        if (SettingsData.getShakeStatus(this)) {

            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

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
            currentAcceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
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

    override fun onResume() {
        if (SettingsData.getShakeStatus(this)) {
            sensorManager?.registerListener(
                sensorListener, sensorManager!!.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER
                ), SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        super.onResume()
    }

    override fun onPause() {
        if (SettingsData.getShakeStatus(this)) {
            sensorManager!!.unregisterListener(sensorListener)
        }
        super.onPause()
    }

    private lateinit var tts: TextToSpeech

    private fun announceCaller(callerName: String?, frequency: Int) {

        try {

            Log.e("Test","announceCaller")

            val audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

            // Lower the ringtone volume
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
            audioManager.setStreamVolume(AudioManager.STREAM_RING,4 , 0)

            val tts = TextToSpeech(this, TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS) {
                    Log.e("Test","Speach Success $frequency")


                    tts.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_ALARM).build()
                    )
                    for (i in 0 until frequency) {
                        tts.speak(
                            "Incoming call from $callerName",
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                        )
                    }
                }else{
                    Log.e("Test","Speach Failed")
                }
            })

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
        }catch (e: Exception)
        {
            UtilFunctions.showToast(this,"Call Announer Not Working")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language if needed
            tts.language = Locale.getDefault()

            // Example: Announce a message
//            announceMessage("Hello, this is a test.")
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }


    private fun loadAnimationFromJsonFile(fileName: String) {
        Log.e("Test","Loading... $fileName")
        val file = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/Download/CallApp/$fileName"
        )

        if (!file.exists()) {
            return
        }

        try {
            val inputStream = FileInputStream(file)

            val composition =
                LottieCompositionFactory.fromJsonInputStreamSync(inputStream, "$fileName").value
            inputStream.close()

            // Set the composition to LottieAnimationView
            binding.callerTheme.setComposition(composition!!)
            binding.callerTheme.playAnimation()
        } catch (e: Exception) {
            UtilFunctions.showToast(this, "Error loading animation")
        }
    }


}