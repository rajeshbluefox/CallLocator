package com.familylocation.mobiletracker.phoneToolsModule;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.familylocation.mobiletracker.BaseActivity;
import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.ads.AdLoadingUtils;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;


public class PhoneToolActivity extends BaseActivity {

//    private LinearLayout llDeviceInfo, llDataUsage, llAudioManager, llSystemUsage;
//    ImageView ivNumberLocation, ivMobileTools, ivSimInfomatoin, ivBankInfomation, ivNearBy, ivFindTrafic, ivNavCompass;

    CardView btAuidoManage,btDeviceInfo,btSystemUsage;
    ImageView btBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_tool);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);

        RelativeLayout rl_adplaceholder= findViewById(R.id.rl_adplaceholder);
        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, PhoneToolActivity.this);

        removeStatusBar();

        btAuidoManage=findViewById(R.id.btAudioManager);
        btDeviceInfo =findViewById(R.id.btDeviceInfo);
        btSystemUsage = findViewById(R.id.btSystemUsage);


        btBack=findViewById(R.id.btBack);

        btAuidoManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneToolActivity.this, AudioControlActivity.class);
                startActivity(intent);

//                MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent, false);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        btDeviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(PhoneToolActivity.this, PhoneInfomationActivity.class);

//                AdLoadingUtils.INSTANCE.showAdLoading(intent4,PhoneToolActivity.this,getLayoutInflater(),PhoneToolActivity.this);

                MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent4, false,getLayoutInflater(),PhoneToolActivity.this);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        btSystemUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(PhoneToolActivity.this, SystemUseActivity.class);

//                AdLoadingUtils.INSTANCE.showAdLoading(intent6,PhoneToolActivity.this,getLayoutInflater(),PhoneToolActivity.this);

                MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent6, false,getLayoutInflater(),PhoneToolActivity.this);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }



    public void removeStatusBar()
    {

        // Call transparentStatusBar method
        JStatusBarUtils.transparentStatusBar(this);

        // Get resources
        Resources resources = getResources();

        // Get a reference to the main layout view
        View mainLayout = findViewById(R.id.titleBarLayout);

        // Call setTopPadding method
        JStatusBarUtils.setTopPadding(resources, mainLayout);

        // Call setTopMargin method
//        JStatusBarUtils.setTopMargin(resources, mainLayout);
    }

    public void toLocation() {
// TODO       Intent intent = new Intent(PhoneToolActivity.this, LocationActivity.class);

//        MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent, false);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 111: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toLocation();
                } else {
                    Toast.makeText(PhoneToolActivity.this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public static void requestPermissionLocation(Activity activity, final int code) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
        }
    }

    public static boolean checkPermissionLocation(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

//    View.OnClickListener mListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.ivNumberLocation:
//
//                    Intent intent = new Intent(PhoneToolActivity.this, NomLocationActivity.class);
//
////                    MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivSimInfomatoin:
//                    Intent intent3 = new Intent(PhoneToolActivity.this, SimInfomationActivity.class);
//
////                    MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent3, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivBankInfomation:
//                    Intent intent2 = new Intent(PhoneToolActivity.this, BankInfomationActivity.class);
//
//                    MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent2, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivNearBy:
//                    Intent intent6 = new Intent(PhoneToolActivity.this, FindTraficActivity.class);
//
//                    MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent6, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivFindTrafic:
//                    if (checkPermissionLocation(PhoneToolActivity.this)) {
//                        toLocation();
//                    } else {
//                        requestPermissionLocation(PhoneToolActivity.this, 111);
//                    }
//                    break;
//                case R.id.ivNavCompass:
//                    Intent intent7 = new Intent(PhoneToolActivity.this, CompassActivity.class);
//
//                    MyApplication.getApplication().displayInterstitialAds(PhoneToolActivity.this, intent7, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//            }
//        }
//    };


}
