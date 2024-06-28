package com.familylocation.mobiletracker.callSettingsModule

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.familylocation.mobiletracker.MyApplication
import com.familylocation.mobiletracker.databinding.ActivityCallAnnouncerBinding
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zSharedPreference.SettingsData
import java.util.Locale

class CallAnnouncerActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityCallAnnouncerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallAnnouncerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Add these line to make status bar transparent
        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources,binding.mainLayout)

        MyApplication.getInstance().loadNativeAd(binding.rlAdplaceholder, this@CallAnnouncerActivity)

        tts = TextToSpeech(this, this)

        initViews()
        onClickListeners()
    }

    private fun initViews() {

        val callAnnouncmentStatus = SettingsData.getCallAnnounceStatus(this)

        binding.btSwitchCallAnnouncer.isChecked = callAnnouncmentStatus

        val callAnnFrequency = SettingsData.getCallAnnouncmentFrequency(this)

        binding.repeatFrequencyBar.progress = callAnnFrequency
        binding.tvRepeatTimes.text = callAnnFrequency.toString()

    }

    private fun onClickListeners() {

        binding.btSwitchCallAnnouncer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val frequency = binding.repeatFrequencyBar.progress
                SettingsData.saveCallAnnounceStatus(this, true, frequency)
            } else {
                val lastFrequency = SettingsData.getCallAnnouncmentFrequency(this)
                SettingsData.saveCallAnnounceStatus(this, false, lastFrequency)
            }
        }

        binding.repeatFrequencyBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                binding.tvRepeatTimes.text = "$progress"
                SettingsData.saveCallAnnFrequency(applicationContext, progress)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Implement if needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Implement if needed
            }
        })

        binding.btPreview.setOnClickListener {
            announceCaller()
        }

        binding.btBack.setOnClickListener {
            finish()
        }

    }

    private lateinit var tts: TextToSpeech

    fun announceCaller() {
        val callerName = "Rajesh"
//            getCallerName(phoneNumber)

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
                val frequency = binding.repeatFrequencyBar.progress

                for (i in 0 until frequency) {
                    tts.speak("Incoming call from SomeOne", TextToSpeech.QUEUE_ADD, null, null)
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

    override fun onDestroy() {
        super.onDestroy()

        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

    }

}