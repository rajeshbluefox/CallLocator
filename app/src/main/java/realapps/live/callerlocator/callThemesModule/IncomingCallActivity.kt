package realapps.live.callerlocator.callThemesModule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.telecom.TelecomManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callSettingsModule.supportFunctions.FlashlightManager
import realapps.live.callerlocator.callThemesModule.supportFunctions.CallEndedReceiver
import realapps.live.callerlocator.databinding.ActivityIncomingCallBinding
import realapps.live.callerlocator.setThemeModule.supportFunctions.ContactDatabaseHelper
import realapps.live.callerlocator.zCommonFuntions.Contact
import realapps.live.callerlocator.zCommonFuntions.ContactManager
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.SettingsData
import java.util.Locale
import java.util.Objects


@AndroidEntryPoint
class IncomingCallActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityIncomingCallBinding

    private lateinit var incomingNumber: String

    private var dbHelper = ContactDatabaseHelper(this)


    private val callEndedReceiver1 = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACTION_CALL_ENDED") {
                // Call ended, finish the activity
                finish()
                stopFlashLight()

            }
        }
    }

    private lateinit var callEndedReceiver: CallEndedReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        UtilFunctions.showToast(this,"Call Coming")
        // Register the BroadcastReceiver
//        val filter = IntentFilter("ACTION_CALL_ENDED")
//        registerReceiver(callEndedReceiver, filter)

        callEndedReceiver = CallEndedReceiver()
        callEndedReceiver.initCallBack(::onCallEnded)
        val filter = IntentFilter("ACTION_CALL_ENDED")
        registerReceiver(callEndedReceiver, filter, RECEIVER_EXPORTED)

        tts = TextToSpeech(this, this)

        setDetails()
        onClickListeners()
    }

    fun onCallEnded() {
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

    fun closeApplication() {
        stopFlashLight()
        finish()
    }

    private fun setDetails() {

        incomingNumber = intent.getStringExtra("incoming_number") ?: ""

        binding.incomingNumberTextView.text = incomingNumber

        findName(incomingNumber)
        findThemeandSet(incomingNumber)
        checkCallAnnouncment(incomingNumber)
        checkCallFlashLight()
        initSensor()
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

        for (contact in allContacts) {
            val number = UtilFunctions.normalizePhoneNumber(contact.number)

            if (number == incomingNumber)
                return contact.name
        }

        return "UnKnown Number"

    }

    private fun findThemeandSet(incomingNumber: String) {

        var theme = dbHelper.getThemeForPhoneNumber(incomingNumber)

        if (theme == 0) {
            val trimmedNumber =
                if (incomingNumber.startsWith("+91")) incomingNumber.substring(3) else incomingNumber
            theme = dbHelper.getThemeForPhoneNumber(trimmedNumber)
        }


        Log.e("Test", "Found Theme $theme")

        val defaultThemeId = SettingsData.getDefaultTheme(this)

        when (theme) {
            -1 -> binding.callerTheme.setAnimation(defaultThemeId)
            0 -> binding.callerTheme.setAnimation(defaultThemeId)
            1 -> binding.callerTheme.setAnimation(R.raw.call_theme_1)
            2 -> binding.callerTheme.setAnimation(R.raw.call_theme_2)
            3 -> binding.callerTheme.setAnimation(R.raw.call_theme_3)
            4 -> binding.callerTheme.setAnimation(R.raw.call_theme_4)

        }
    }

    fun checkCallFlashLight() {
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

    fun initSensor() {
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

        val audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

        // Lower the ringtone volume
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 2, 0)

        val tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                tts.setAudioAttributes(
                    AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ALARM).build()
                )
                for (i in 0 until frequency) {
                    tts.speak("Incoming call from $callerName", TextToSpeech.QUEUE_ADD, null, null)
                }
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

}