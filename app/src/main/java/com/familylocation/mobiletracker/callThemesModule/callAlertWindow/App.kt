package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.familylocation.mobiletracker.R

class App : Application() {

    lateinit var context : Context
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        context.setTheme(R.style.AppTheme)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        fun getContext(): Context {
            return context!!
        }
    }
}
