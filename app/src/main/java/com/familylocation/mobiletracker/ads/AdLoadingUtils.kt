package com.familylocation.mobiletracker.ads

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.familylocation.mobiletracker.MyApplication

object AdLoadingUtils {


    fun showAdLoading(intent: Intent,act: Activity,layoutInflater: LayoutInflater,context: Context)
    {
        val adsLoadingDialog = AdsLoadingDialog(layoutInflater,context)
        adsLoadingDialog.openAdsLoading()

        Handler(Looper.getMainLooper()).postDelayed({

//           adsLoadingDialog.closeAdsLoading()
//
//            MyApplication.getApplication()
//                .displayInterstitialAds(act, intent, false)

        }, 3000)
    }

}