package realapps.live.callerlocator.zCommonFuntions

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.appcompat.app.AppCompatActivity

object StatusBarUtils {

    fun transparentStatusBar(activity: AppCompatActivity) {
        activity.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        activity.window.statusBarColor = Color.TRANSPARENT

//        activity.window().getDecorView().getWindowInsetsController().setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS);

    }



    fun getStatusBarHeight(resources : Resources): Int {
        var result = 0
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

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