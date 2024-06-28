package com.familylocation.mobiletracker.nearByPlacesModule;

import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

import edu.arbelkilani.compass.Compass;
import edu.arbelkilani.compass.CompassListener;
import com.familylocation.mobiletracker.BaseActivity;
import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;


public class CompassActivity extends BaseActivity {

    private CardView compass;
//    ImageView ivNumberLocation, ivMobileTools, ivSimInfomatoin, ivBankInfomation, ivNearBy, ivFindTrafic, ivNavCompass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        removeStatusBar();

//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);

        RelativeLayout rl_adplaceholder= findViewById(R.id.rl_adplaceholder);
        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, CompassActivity.this);


        Compass compass = findViewById(R.id.compass_view);
        compass.setListener(new CompassListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.d("TAG", "onSensorChanged : " + event);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d("TAG", "onAccuracyChanged : sensor : " + sensor);
                Log.d("TAG", "onAccuracyChanged : accuracy : " + accuracy);
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

        ImageView btBack;
        btBack=findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


//    View.OnClickListener mListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.ivNumberLocation:
//
//                    Intent intent = new Intent(CompassActivity.this, NomLocationActivity.class);
//
//                    MyApplication.getApplication().displayInterstitialAds(CompassActivity.this, intent, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivMobileTools:
//
//                    Intent intent4 = new Intent(CompassActivity.this, PhoneToolActivity.class);
//
//                    MyApplication.getApplication().displayInterstitialAds(CompassActivity.this, intent4, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivSimInfomatoin:
//                    Intent intent3 = new Intent(CompassActivity.this, SimInfomationActivity.class);
//
//                    MyApplication.getApplication().displayInterstitialAds(CompassActivity.this, intent3, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivBankInfomation:
//                    Intent intent2 = new Intent(CompassActivity.this, BankInfomationActivity.class);
//                    MyApplication.getApplication().displayInterstitialAds(CompassActivity.this, intent2, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivNearBy:
//                    Intent intent6 = new Intent(CompassActivity.this, FindTraficActivity.class);
//
//                    MyApplication.getApplication().displayInterstitialAds(CompassActivity.this, intent6, false);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                    break;
//                case R.id.ivFindTrafic:
//                    if (checkPermissionLocation(CompassActivity.this)) {
//                        toLocation();
//                    } else {
//                        requestPermissionLocation(CompassActivity.this, 111);
//                    }
//                    break;
//            }
//        }
//    };

//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 111: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    toLocation();
//                } else {
//                    Toast.makeText(CompassActivity.this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

//    public static void requestPermissionLocation(Activity activity, final int code) {
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
//        } else {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
//        }
//    }

//    public static boolean checkPermissionLocation(Activity activity) {
//        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (result == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    public void toLocation() {
//        Intent intent = new Intent(CompassActivity.this, LocationActivity.class);
//
//        MyApplication.getApplication().displayInterstitialAds(CompassActivity.this, intent, false);
//        overridePendingTransition(R.anim.enter, R.anim.exit);
//
//
//    }

}
