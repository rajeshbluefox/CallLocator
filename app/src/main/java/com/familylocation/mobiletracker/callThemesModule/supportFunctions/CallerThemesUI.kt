package com.familylocation.mobiletracker.callThemesModule.supportFunctions

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.familylocation.mobiletracker.databinding.FragmentCallThemesBinding
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils

class CallerThemesUI(
    val context: Context,
    private val binding: FragmentCallThemesBinding
) {

    fun setTopPadding(resources: Resources,view: View) {
        val topPadding = StatusBarUtils.getStatusBarHeight(resources) + 12

        view.setPadding(
            view.paddingLeft,
            topPadding,
            view.paddingRight,
            view.paddingBottom
        )
    }
}