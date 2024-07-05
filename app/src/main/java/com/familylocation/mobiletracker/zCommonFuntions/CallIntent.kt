package com.familylocation.mobiletracker.zCommonFuntions

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.familylocation.mobiletracker.bankInformationModule.BankInfomationActivity
import com.familylocation.mobiletracker.callLocatorModule.FriendRequestActivity
import com.familylocation.mobiletracker.callSettingsModule.BlockedNumbersActivity
import com.familylocation.mobiletracker.callSettingsModule.CallAnnouncerActivity
import com.familylocation.mobiletracker.callSettingsModule.CallFlashActivity
import com.familylocation.mobiletracker.homeModule.ControllingActivity
import com.familylocation.mobiletracker.setThemeModule.SetThemeActivity
import com.familylocation.mobiletracker.homeModule.HomeActivity
import com.familylocation.mobiletracker.loginModule.LoginActivity
import com.familylocation.mobiletracker.nearByPlacesModule.CompassActivity
import com.familylocation.mobiletracker.nearByPlacesModule.FindTraficActivity
import com.familylocation.mobiletracker.nearByPlacesModule.TrafficActivity
import com.familylocation.mobiletracker.phoneToolsModule.PhoneToolActivity
import com.familylocation.mobiletracker.profileModule.ProfileActivity
import com.familylocation.mobiletracker.setThemeModule.SelectContactsActivity
import com.familylocation.mobiletracker.simInfoModule.SimInfomationActivity

object CallIntent {

    fun goToControllingActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, ControllingActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToHomeActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }



    fun goToLoginActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, LoginActivity::class.java)
        if (killMe) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

    }

    fun goToSetThemeActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, SetThemeActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToSelectContactsActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, SelectContactsActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToCallFlashLightActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, CallFlashActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToCallAnnouncmentActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, CallAnnouncerActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToFriendRequestActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, FriendRequestActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToProfileActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToCallBlockingActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, BlockedNumbersActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToPhoneToolActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, PhoneToolActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToSimInfomationActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, SimInfomationActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToBankInfomationActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, BankInfomationActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToFindTraficActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, FindTraficActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToTrafficActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, TrafficActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }

    fun goToCompassActivity(context: Context, killMe: Boolean, activity: Activity) {
        val intent = Intent(context, CompassActivity::class.java)
        context.startActivity(intent)
        if (killMe) activity.finish()
    }
}
