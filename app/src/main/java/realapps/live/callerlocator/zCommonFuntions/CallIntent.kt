package realapps.live.callerlocator.zCommonFuntions

import android.app.Activity
import android.content.Context
import android.content.Intent
import realapps.live.callerlocator.callLocatorModule.FriendRequestActivity
import realapps.live.callerlocator.callSettingsModule.BlockedNumbersActivity
import realapps.live.callerlocator.callSettingsModule.CallAnnouncerActivity
import realapps.live.callerlocator.callSettingsModule.CallFlashActivity
import realapps.live.callerlocator.setThemeModule.SetThemeActivity
import realapps.live.callerlocator.homeModule.HomeActivity
import realapps.live.callerlocator.loginModule.LoginActivity
import realapps.live.callerlocator.profileModule.ProfileActivity
import realapps.live.callerlocator.setThemeModule.SelectContactsActivity

object CallIntent {
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

}
