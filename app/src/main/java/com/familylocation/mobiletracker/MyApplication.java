package com.familylocation.mobiletracker;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

import com.familylocation.mobiletracker.ads.AdsLoadingDialog;
import com.familylocation.mobiletracker.ads.SupportedClass;
import com.familylocation.mobiletracker.nativenotifier.EventNotifier;
import com.familylocation.mobiletracker.nativenotifier.EventTypes;
import com.familylocation.mobiletracker.nativenotifier.NotifierFactory;
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions;
import com.familylocation.mobiletracker.zSharedPreference.LoginData;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private static MyApplication mInstance;
    public Context sContext = null;

    private static MyApplication myApplication;
    public static final String ADMOB_TAG = "ADSTAG";
    public static boolean isShowingFullScreenAd = false;
    private Dialog loadAdsDialog;
    //InterstitialAd
    public AdRequest adRequest;
    public InterstitialAd mInterstitialAd;

    //NativeAd
    public List<NativeAd> mNativeAdsGHome = new ArrayList<>();
    public ArrayList<String> mAdsId = new ArrayList<>();

    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    //GDPR
    private ConsentInformation consentInformation;
    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        mInstance = this;
        setApplication(this);

//        List<String> testDeviceIds = Arrays.asList("0EE3E724C87B1B344670CBB104308A4D");
//        .setTestDeviceIds(testDeviceIds)

        RequestConfiguration configuration =
                new RequestConfiguration.Builder().build();
        MobileAds.setRequestConfiguration(configuration);


        adRequest = new AdRequest.Builder().build();
        this.registerActivityLifecycleCallbacks(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager(this);

        initGDPRConsent();

    }

    void initGDPRConsent() {
        // Create a ConsentRequestParameters object.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                currentActivity,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            currentActivity,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w("Test", String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initializeMobileAdsSdk();
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w("Test", String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        initADS();
    }

    void initADS() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Log.e(ADMOB_TAG, "Ad initialize ===> Completed");

                getAdId();
            }
        });

    }

    void getAdId() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(sContext);
                    String adId = adInfo != null ? adInfo.getId() : null;
                    // Use the advertising id
                    Log.e(ADMOB_TAG, adId);

                } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
                    // Error handling if needed
                }
            }
        });

    }

    public static MyApplication getApplication() {
        return myApplication;
    }

    public static void setApplication(MyApplication application) {
        myApplication = application;
    }


    public static synchronized MyApplication getInstance() {
        MyApplication myApplication;
        synchronized (MyApplication.class) {
            synchronized (MyApplication.class) {
                myApplication = mInstance;
            }
        }
        return myApplication;
    }

    public void loadInterstitialAd() {

        if(mInterstitialAd==null) {
            String adUnitId = "";
            adUnitId = getString(R.string.admob_interstitial_ads_id_test);

            Log.e(ADMOB_TAG, "InterstitialAd  Loading...===> " + adUnitId);
            if (TextUtils.isEmpty(adUnitId)) {
                return;
            }

            InterstitialAd.load(this, adUnitId, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                            Log.e(ADMOB_TAG, "InterstitialAd ===> onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i(ADMOB_TAG, "InterstitialAd ===> " + loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        }
    }


    public void displayInterstitialAds(final Activity act, final Intent intent, final boolean isFinished,LayoutInflater layoutInflater,Context context) {

//        int count = SupportedClass.getInterstitialAdsCount();
//        && count % 2 == 0
        int count = LoginData.INSTANCE.getAdCount();

        Log.e("Test","Int Ad "+count);

        if (mInterstitialAd != null && count % 2 != 0) {

            Log.e("Test","Ad is Not Empty");
            showAdLoading(act, layoutInflater, context,intent,isFinished);
        } else {
            Log.e("Test", "InterstitialAd ===> " + "Start Activity but Ad not load.");
            doNextAction(act, intent, isFinished);
            isShowingFullScreenAd = false;
        }

        LoginData.INSTANCE.setAdCount(count+1);
//        SupportedClass.setInterstitialAdsCount(count + 1);

        if(mInterstitialAd!=null)
        {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.e("Test", "InterstitialAd ===> The ad was dismissed.");
                    isShowingFullScreenAd = false;
                    doNextAction(act, intent, isFinished);
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.e("Test", "InterstitialAd ===> The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                    Log.e("Test", "InterstitialAd ===> The ad was shown.");
                }
            });
        }

    }

    public void displayInterstitialAdsWitoutIntent(final Activity act,LayoutInflater layoutInflater,Context context) {

        int count = SupportedClass.getInterstitialAdsCount();
        if (mInterstitialAd != null && count % 2 == 0) {
            mInterstitialAd.show(act);
            isShowingFullScreenAd = true;

//            showAdLoading(act,  layoutInflater, context);
        } else {
            Log.i(ADMOB_TAG, "InterstitialAd ===> " + "Start Activity but Ad not load.");
//            doNextAction(act, intent, isFinished);
            isShowingFullScreenAd = false;
        }
        SupportedClass.setInterstitialAdsCount(count + 1);

        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.e("Test", "InterstitialAd ===> The ad was dismissed. displayInterstitialAdsWitoutIntent");
                    isShowingFullScreenAd = false;
                    loadInterstitialAd();
//                    doNextAction(act, intent, isFinished);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.d(ADMOB_TAG, "InterstitialAd ===> The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                    Log.d(ADMOB_TAG, "InterstitialAd ===> The ad was shown.");
                }
            });
        }
    }

    void showAdLoading(final Activity act,LayoutInflater layoutInflater,Context context,final Intent intent, final boolean isFinished)
    {
        try {

            AdsLoadingDialog adsLoadingDialog = new AdsLoadingDialog(layoutInflater, context);
            adsLoadingDialog.openAdsLoading();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Your code here
                    adsLoadingDialog.closeAdsLoading();
//                    doNextAction(act, intent, isFinished);
                    mInterstitialAd.show(act);
                    isShowingFullScreenAd = true;
                }
            }, 3000);
        }catch (Exception e)
        {
            Log.e("Test",e.getMessage());
        }
    }

    public void loadInterstitialAdExit(Activity act, final Intent intent, final boolean isFinished) {

        loadAdsDialog = new Dialog(act);
        loadAdsDialog.setContentView(R.layout.layout_loading);
        loadAdsDialog.setCanceledOnTouchOutside(false);
        loadAdsDialog.setCancelable(false);
        loadAdsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        loadAdsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadAdsDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

        if (!act.isFinishing()) {
            loadAdsDialog.show();
        }
        String adUnitId = "";
        adUnitId = getString(R.string.admob_interstitial_ads_id_test);

        Log.i(ADMOB_TAG, "InterstitialAd ===> " + adUnitId);
        if (TextUtils.isEmpty(adUnitId)) {
            loadAdsDialog.dismiss();
            return;
        }

        InterstitialAd.load(this, adUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.e("Test", "InterstitialAd ===> onAdLoaded");
                        displayInterstitialAdsExit(act, intent, isFinished);
                        loadAdsDialog.dismiss();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.e("Test", "InterstitialAd ===> " + loadAdError.getMessage());
                        mInterstitialAd = null;
                        loadAdsDialog.dismiss();
                        doNextAction(act, intent, isFinished);
                    }
                });
    }

    public void displayInterstitialAdsExit(Activity act, final Intent intent, final boolean isFinished) {

        if (mInterstitialAd != null) {
            mInterstitialAd.show(act);
            isShowingFullScreenAd = true;
        } else {
            Log.i(ADMOB_TAG, "InterstitialAd ===> " + "Start Activity but Ad not load.");
            loadInterstitialAdExit(act, intent, isFinished);
            isShowingFullScreenAd = false;
        }

        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.e("Test", "InterstitialAd ===> The ad was dismissed. displayInterstitialAdsExit");
                    isShowingFullScreenAd = false;
                    doNextAction(act, intent, isFinished);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.d(ADMOB_TAG, "InterstitialAd ===> The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                    Log.d(ADMOB_TAG, "InterstitialAd ===> The ad was shown.");
                }
            });
        }
    }

    public void doNextAction(final Activity act, final Intent intent, final boolean isFinished) {
        if (intent != null)
            act.startActivity(intent);
        if (isFinished) {
            if (act != null && !act.isFinishing())
                act.finish();
        }
    }


    public List<NativeAd> getGNativeHome() {
        return mNativeAdsGHome;
    }

    public ArrayList<String> getmAdsId() {
        return mAdsId;
    }


    public void loadNativeOptional(int adxCount) {
        if (adxCount == 0) {
            mNativeAdsGHome = new ArrayList<>();
            Log.i(ADMOB_TAG, "NativeAds ID ==> 0");
        }
        AdLoader.Builder builder;

        String adUnitId = "";
        adUnitId = getString(R.string.admob_native_Ad_test);

        Log.i(ADMOB_TAG, "NativeAds ID ==> " + adUnitId);

        if (TextUtils.isEmpty(adUnitId)) {
            return;
        }

        builder = new AdLoader.Builder(this, adUnitId);

        builder.forNativeAd(nativeAd -> {
            mNativeAdsGHome.add(nativeAd);

            int nextConunt = adxCount + 1;
            if (nextConunt < mAdsId.size()) {
                loadNativeOptional(nextConunt);
            }
            if (nextConunt == mAdsId.size()) {
                Log.i(ADMOB_TAG, "NativeAds ID => " + "last ==> ");
                EventNotifier notifier = NotifierFactory.getInstance().getNotifier(NotifierFactory.EVENT_NOTIFIER_AD_STATUS);
                notifier.eventNotify(EventTypes.EVENT_AD_LOADED_NATIVE, null);
            }
        }).withAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i(ADMOB_TAG, "NativeAds ID ==> " + "Successs to onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.i(ADMOB_TAG, "NativeAds ID => " + loadAdError.getMessage());
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.i(ADMOB_TAG, "onAdFailedToLoad ==> " + adError.getMessage());
                if (mNativeAdsGHome.size() == 0) {
                    Log.i(ADMOB_TAG, "onAdFailedToLoad mNativeAdsGHome.size() ==>  0");
                } else {
                    Log.i(ADMOB_TAG, "onAdFailedToLoad mNativeAdsGHome.size() ==>  Event");
                }

            }
        }).build();

        AdRequest.Builder builerRe = new AdRequest.Builder();
        adLoader.loadAd(builerRe.build());
    }

    public void loadNativeAds(RelativeLayout fl_adplaceholder, Activity activity) {
        NativeAdView adView;
        if (MyApplication.getInstance().getGNativeHome() != null && MyApplication.getInstance().getGNativeHome().size() > 0 && MyApplication.getInstance().getGNativeHome().get(0) != null) {
            NativeAd nativeAd = MyApplication.getInstance().getGNativeHome().get(0);

            adView = (NativeAdView) LayoutInflater.from(this).inflate(R.layout.ads_native_list, null);
            populateUnifiedNativeAdListView(nativeAd, adView);

            fl_adplaceholder.removeAllViews();
            fl_adplaceholder.addView(adView);
            fl_adplaceholder.setVisibility(View.VISIBLE);
        } else {
            fl_adplaceholder.setVisibility(View.GONE);
        }

    }

    private void populateUnifiedNativeAdListView(NativeAd nativeAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        try {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView()))
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.getStoreView().setVisibility(View.GONE);
        adView.getPriceView().setVisibility(View.GONE);

        adView.setNativeAd(nativeAd);

        VideoController vc = Objects.requireNonNull(nativeAd.getMediaContent()).getVideoController();

        if (vc.hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    public void loadNativeAd(RelativeLayout fl_adplaceholder, Activity activity) {
        try {
            if (MyApplication.getInstance().getGNativeHome() != null && MyApplication.getInstance().getGNativeHome().size() > 0) {
                NativeAd nativeAd = MyApplication.getInstance().getGNativeHome().get(0);
                NativeAdView adView = (NativeAdView) LayoutInflater.from(activity).inflate(R.layout.ad_unified, null);
                BaseActivity.populateUnifiedNativeAdView(nativeAd, adView);
                fl_adplaceholder.setVisibility(View.VISIBLE);
                fl_adplaceholder.removeAllViews();
               fl_adplaceholder.addView(adView);
                Log.i(ADMOB_TAG, "NativeAds ID ==> " + "Successs to show");

            } else {
                fl_adplaceholder.setVisibility(View.GONE);
                Log.i(ADMOB_TAG, "NativeAds ID ==> " + "Fail to show");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fl_adplaceholder.setVisibility(View.GONE);
            Log.i(ADMOB_TAG, "NativeAds ID ==> " + "Fail " + e.getMessage());

        }

    }
    /////////////////////////Open Ad Code//////////////////

//        @OnLifecycleEvent(Lifecycle.Event.ON_START)
//        protected void onMoveToForeground() {
//            // Show the ad (if available) when the app moves to foreground.
//            appOpenAdManager.showAdIfAvailable(currentActivity);
//        }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.e("Test","Showing Open Ad");
        DefaultLifecycleObserver.super.onStart(owner);
        // Show the ad (if available) when the app moves to foreground.

        if(!UtilFunctions.INSTANCE.isInPermissionFlow()) {
            appOpenAdManager.showAdIfAvailable(currentActivity);
        }

    }

    void showOpenAd()
    {
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    class AppOpenAdManager {

        private static final String LOG_TAG = "AppOpenAdManager";
            private static final String AD_UNIT_ID = "ca-app-pub-4644402642324922/3240950747";

//        private final String AD_UNIT_ID = getString(R.string.admob_open_Ad_test);


        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        public long loadTime = 0;

        /**
         * Constructor.
         */
        public AppOpenAdManager(MyApplication myApplication) {
        }

        /**
         * Request an ad.
         */
        private void loadAd(Context context) {
            // We will implement this below.
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context, AD_UNIT_ID, request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            // Called when an app open ad has loaded.
                            Log.e("Test", "Ad was loaded.");
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            // Called when an app open ad has failed to load.
                            Log.d(LOG_TAG, loadAdError.getMessage());
                            isLoadingAd = false;
                        }
                    });
        }

        /**
         * Shows the ad if one isn't already showing.
         */
        public void showAdIfAvailable(
                @NonNull final Activity activity
                /*@NonNull OnShowAdCompleteListener onShowAdCompleteListener */) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                //** onShowAdCompleteListener.onShowAdComplete();
                loadAd(MyApplication.this);
                return;
            }

            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            // Set the reference to null so isAdAvailable() returns false.
                            Log.d(LOG_TAG, "Ad dismissed fullscreen content.");
                            appOpenAd = null;
                            isShowingAd = false;

                            //** onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when fullscreen content failed to show.
                            // Set the reference to null so isAdAvailable() returns false.
                            Log.d(LOG_TAG, adError.getMessage());
                            appOpenAd = null;
                            isShowingAd = false;

                            //**   onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            Log.d(LOG_TAG, "Ad showed fullscreen content.");
                        }
                    });
            isShowingAd = true;
            appOpenAd.show(activity);
        }


        /**
         * Check if ad exists and can be shown.
         */
        private boolean isAdAvailable() {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - this.loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }
    }


    /**
     * ActivityLifecycleCallback methods.
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // Updating the currentActivity only when an ad is not showing.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
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
    }

}
