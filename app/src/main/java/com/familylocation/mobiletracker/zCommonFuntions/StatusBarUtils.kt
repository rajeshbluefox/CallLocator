package com.familylocation.mobiletracker.zCommonFuntions

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

object StatusBarUtils {

    fun transparentStatusBar(activity: AppCompatActivity) {
        activity.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        activity.window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API level 30) and above
            activity.window.insetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS,
                APPEARANCE_LIGHT_STATUS_BARS
            )
        } else
        // For Android 6.0 (API level 23) and above, but before Android 11
            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }


    fun getStatusBarHeight(resources: Resources): Int {
        var result = 0
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun setTopPadding(resources: Resources, view: View) {
        val topPadding = getStatusBarHeight(resources) + 12

        view.setPadding(
            view.paddingLeft,
            topPadding,
            view.paddingRight,
            view.paddingBottom
        )
    }

    fun setTopMargin(resources: Resources, view: View) {
        // Assuming tabLayout is your TabLayout instance
        val params = view.layoutParams as LinearLayout.LayoutParams
        val marginTopInPixels =
            resources.getDimensionPixelSize(getStatusBarHeight(resources)) // Adjust this according to your needs
        params.topMargin = marginTopInPixels
        view.layoutParams = params
    }
}