package com.familylocation.mobiletracker.zSharedPreference

import android.content.Context
import com.familylocation.mobiletracker.zConstants.Constants

object SettingsData {

    private const val PREFS_NAME = "CallerSettings"

    fun saveCallThemeStatus(context: Context, value: Boolean) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(Constants.CALL_THEME, value).apply()
    }

    fun getCallThemeStatus(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(Constants.CALL_THEME, false)
    }

    fun saveDefaultTheme(context: Context, value: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.CALL_DEFAULT_THEME, value).apply()
    }

    fun getDefaultTheme(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(Constants.CALL_DEFAULT_THEME, "EMPTY")?:"EMPTY"
    }

    fun saveCallFlashLightStatus(context: Context, value: Boolean,frequency: Int) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(Constants.CALL_FLASH_LIGHT, value).apply()
        editor.putInt(Constants.CALL_FLASH_LIGHT_FREQUENCY,frequency).apply()
    }

    fun getCallFlashLightStatus(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(Constants.CALL_FLASH_LIGHT, false)
    }

    fun getCallLightFrequency(context: Context):Int{
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt(Constants.CALL_FLASH_LIGHT_FREQUENCY, 200)
    }

    fun saveShakeStatus(context: Context, value: Boolean) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(Constants.SHAKE_TO_STOP, value).apply()
    }

    fun getShakeStatus(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(Constants.SHAKE_TO_STOP, false)
    }

    fun saveCallAnnounceStatus(context: Context, value: Boolean,repeatTime: Int) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(Constants.CALL_ANNOUNCMENT, value).apply()
        editor.putInt(Constants.CALL_ANNOUNCMENT_FREQUENCY,repeatTime).apply()

    }

    fun saveCallAnnFrequency(context: Context,repeatTime: Int)
    {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(Constants.CALL_ANNOUNCMENT_FREQUENCY,repeatTime).apply()

    }

    fun getCallAnnounceStatus(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(Constants.CALL_ANNOUNCMENT, false)
    }

    fun getCallAnnouncmentFrequency(context: Context):Int{
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt(Constants.CALL_ANNOUNCMENT_FREQUENCY, 3)
    }


}