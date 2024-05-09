package realapps.live.callerlocator.callThemesModule.supportFunctions

import android.content.Context
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.os.Environment
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
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callSettingsModule.supportFunctions.FlashlightManager
import realapps.live.callerlocator.setThemeModule.supportFunctions.ContactDatabaseHelper
import realapps.live.callerlocator.zCommonFuntions.Contact
import realapps.live.callerlocator.zCommonFuntions.ContactManager
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.SettingsData
import java.io.File
import java.io.FileInputStream
import java.util.Locale
import java.util.Objects

class Window(private val context: Context) : TextToSpeech.OnInitListener {

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

    private lateinit var callEndedReceiver: CallEndedReceiver

    private var tts: TextToSpeech


    init {
//        callEndedReceiver = CallEndedReceiver()
//        callEndedReceiver.initCallBack(::onCallEnded)
//        val filter = IntentFilter("ACTION_CALL_ENDED")
//        context.registerReceiver(callEndedReceiver, filter, AppCompatActivity.RECEIVER_EXPORTED)


        tts = TextToSpeech(context, this)

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = layoutInflater.inflate(R.layout.activity_incoming_call, null)

        tvCallerName = mView.findViewById<TextView>(R.id.userName)
        tvIncomingNumber = mView.findViewById<TextView>(R.id.incomingNumberTextView)


        callerTheme = mView.findViewById<LottieAnimationView>(R.id.callerTheme)

        declineBt = mView.findViewById<LottieAnimationView>(R.id.btDecline)
        acceptBt = mView.findViewById<LottieAnimationView>(R.id.btAccept)

        acceptBt.setOnClickListener {
            val tm = context
                .getSystemService(AppCompatActivity.TELECOM_SERVICE) as TelecomManager
            tm.acceptRingingCall()
            UtilFunctions.showToast(context, "Accepting")
            close()
        }

        declineBt.setOnClickListener {
            val telecomManager = context.getSystemService(TelecomManager::class.java)
            telecomManager?.endCall()
            UtilFunctions.showToast(context, "Rejected")
            close()
        }


        // Set the layout parameters to match the parent's size
        val parentLayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mView.layoutParams = parentLayoutParams

        mParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }
        mParams.gravity = Gravity.CENTER
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun open(incomingNumber: String) {
        try {
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    setDetails(incomingNumber)
                    onOpened()
                    mWindowManager.addView(mView, mParams)
                }
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    fun close() {
        try {
            Log.e("Test", "Closing Window")
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            mView.invalidate()
            onClosed()
            stopAnnouncment()
            stopFlashLight()
//            context.unregisterReceiver(callEndedReceiver)

            (mView.parent as? ViewGroup)?.removeAllViews()
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }

    fun stopAnnouncment() {
        tts.stop()
//        tts.shutdown()
    }

    private fun onCallEnded() {
        close()
    }

    private fun setDetails(incomingNumber: String) {

        tvIncomingNumber.text = incomingNumber

//        findName(incomingNumber)
//        findThemeandSet(incomingNumber)
//        checkCallAnnouncment(incomingNumber)
//        checkCallFlashLight()
//        initSensor()
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

        var locPhoneNumber: String = UtilFunctions.makePhoneNumber10(incomingNumber)

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
                    loadAnimationFromJsonFile(defaultThemeId)
            }

            else -> {
                val themeId = "call_theme_$theme.json"
                loadAnimationFromJsonFile(themeId)

            }

        }
    }

    private fun loadAnimationFromJsonFile(fileName: String) {
        Log.e("Test", "Loading... $fileName")
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
            callerTheme.setComposition(composition!!)
            callerTheme.playAnimation()
        } catch (e: Exception) {
            UtilFunctions.showToast(context, "Error loading animation")
        }
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

    fun onOpened() {
        if (SettingsData.getShakeStatus(context)) {
            sensorManager?.registerListener(
                sensorListener, sensorManager!!.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER
                ), SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun onClosed() {
        if (SettingsData.getShakeStatus(context)) {
            sensorManager!!.unregisterListener(sensorListener)
        }
    }

    //////////////////////////////Announcment Code/////////////////

    private fun checkCallAnnouncment(incomingNumber: String) {

        val callAnnouncmentStatus = SettingsData.getCallAnnounceStatus(context)

        if (callAnnouncmentStatus) {
            val callerName = getCallerName(incomingNumber)
            val callAnnFreq = SettingsData.getCallAnnouncmentFrequency(context)
            announceCaller(callerName, callAnnFreq)
        }
    }

    private fun announceCaller(callerName: String?, frequency: Int) {

        try {

            Log.e("Test","announceCaller")

            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

            // Lower the ringtone volume
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
            audioManager.setStreamVolume(AudioManager.STREAM_RING,4 , 0)

            val tts = TextToSpeech(context, TextToSpeech.OnInitListener {
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
            UtilFunctions.showToast(context,"Call Announer Not Working")
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

}