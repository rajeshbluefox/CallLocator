package com.familylocation.mobiletracker.zSharedPreference

import android.content.Context
import com.familylocation.mobiletracker.zConstants.Constants

object LoginData {
    var adCount = 1

    private const val PREFS_NAME = "ParishLoginData"

    fun saveTutorialStatus(context: Context, value: Boolean) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(Constants.TUTORIAL_STATUS, value).apply()
    }

    fun getTutorialStatus(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(Constants.TUTORIAL_STATUS, false)
    }

    fun saveUserLoginStatus(context: Context, value: Boolean) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(Constants.LOGIN_STATUS, value).apply()
    }

    fun getUserLoginStatus(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(Constants.LOGIN_STATUS, false)
    }

    fun saveUserDetails(context: Context,userType: Int,userCode: Int)
    {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(Constants.USER_TYPE, userType).apply()
        editor.putInt(Constants.USER_ID,userCode).apply()
    }

    fun getUserType(context: Context): Int {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt(Constants.USER_TYPE, -1)
    }

    fun getUserCode(context: Context): Int {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt(Constants.USER_ID, -1)
    }

    fun saveUserName(context: Context, value: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.USER_NAME, value).apply()
    }

    fun getUserName(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(Constants.USER_NAME, "EMPTY")?:"EMPTY"
    }

    fun saveUserPhone(context: Context, value: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.USER_PHONE, value).apply()
    }

    fun getUserPhone(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(Constants.USER_PHONE, "EMPTY")?:"EMPTY"
    }

    fun saveUserId(context: Context, value: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.USER_ID, value).apply()
    }

    fun getUserId(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(Constants.USER_ID, "EMPTY")?:"EMPTY"
    }

    fun saveFCMToken(context: Context, imageURL: String){
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.USER_IMAGE, imageURL).apply()
    }

    fun getFCMToken(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(Constants.USER_IMAGE, "EMPTY")?:"EMPTY"
    }

    fun clear(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear().apply()
    }
}
