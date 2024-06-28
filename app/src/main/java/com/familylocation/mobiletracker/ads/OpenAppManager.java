package com.familylocation.mobiletracker.ads;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import org.jetbrains.annotations.NotNull;

import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;

public class OpenAppManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private static boolean isShowingAd = false;
    private final MyApplication myApplication;
    private AppOpenAd appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private Activity currentActivity;

    public OpenAppManager(MyApplication myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.i(MyApplication.ADMOB_TAG, "Open AD ==> " + " onStart");
    }

    public void showAdIfAvailable() {

        if (!isShowingAd && !MyApplication.isShowingFullScreenAd && isAdAvailable()) {
            Log.i(MyApplication.ADMOB_TAG, "Open AD ==> " + " Will show Ad");
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();
                            Log.d(MyApplication.ADMOB_TAG, "Open AD ===> The ad was dismissed.");
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NotNull AdError adError) {
                            Log.d(MyApplication.ADMOB_TAG, "Open AD ===> The ad failed to show.");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                            Log.d(MyApplication.ADMOB_TAG, "Open AD ===> The ad was shown.");
                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);

        } else {
            Log.i(MyApplication.ADMOB_TAG, "Open App ID ==> " + " Enable");
            fetchAd();
        }
    }


    public void fetchAd() {
        if (isAdAvailable()) {
            return;
        }
        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NotNull AppOpenAd ad) {
                        appOpenAd = ad;
                        Log.i(MyApplication.ADMOB_TAG, "Open App ID Load == > onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.i(MyApplication.ADMOB_TAG, "Open App ID Fail Load == > " + loadAdError.getMessage());
                    }
                };
        AdRequest request = getAdRequest();

        String adUnitId = "";
        adUnitId = myApplication.getString(R.string.admob_open_Ad_test);

        if (TextUtils.isEmpty(adUnitId)) {
            return;
        }
        Log.i(MyApplication.ADMOB_TAG, "Open App ID ==> " + adUnitId);
        AppOpenAd.load(myApplication, adUnitId, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }


    public boolean isAdAvailable() {
        return appOpenAd != null;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }
}