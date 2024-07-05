package com.familylocation.mobiletracker.zCommonFuntions;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
public class JStatusBarUtils {

    public static void transparentStatusBar(AppCompatActivity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API level 30) and above
            WindowInsetsController insetsController = activity.getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            // For Android 6.0 (API level 23) and above, but before Android 11
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static int getStatusBarHeight(Resources resources) {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setTopPadding(Resources resources, View view) {
        int topPadding = getStatusBarHeight(resources) + 12;

        view.setPadding(view.getPaddingLeft(), topPadding, view.getPaddingRight(), view.getPaddingBottom());
    }

    public static void setTopMargin(Resources resources, View view) {
        // Assuming view's parent is a LinearLayout
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        int marginTopInPixels = resources.getDimensionPixelSize(getStatusBarHeight(resources));
        params.topMargin = marginTopInPixels;
        view.setLayoutParams(params);
    }
}