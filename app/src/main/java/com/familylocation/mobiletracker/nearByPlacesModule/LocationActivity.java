package com.familylocation.mobiletracker.nearByPlacesModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.familylocation.mobiletracker.BaseActivity;
import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;

public class LocationActivity extends BaseActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    TextView latitude,longitude,city,state,country;
    private LocationRequest mLocationRequest;
    private static final String TAG = "Act_Location";
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private List<Address> addresses;
    private Geocoder geocoder;
    private LatLng currentLocation;
    private int geocoderMaxResults = 1;
    private String mLastUpdateTime;
    private RelativeLayout viewLocationBtn;


    private static final String DIALOG_ERROR = "dialog_error";

    LocationManager locationManager;
    private boolean mRequestingLocationUpdates = false;
    private boolean mResolvingError = false;

   ImageView ivBack;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RelativeLayout rl_adplaceholder= findViewById(R.id.rl_adplaceholder);
        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, LocationActivity.this);

//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);

        viewLocationBtn = findViewById(R.id.location_view_btn);

        viewLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                performNext();
            }
        });

        ivBack = findViewById(R.id.btBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (isGooglePlayServicesAvailable()) {
            locationManager = (LocationManager) getSystemService("location");
        } else {
            locationManager = (LocationManager) getSystemService("location");
        }


        if (Build.VERSION.SDK_INT >= 9) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        }

        createLocationRequest();

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 111: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startActivity(new Intent(LocationActivity.this, MapActivity.class));
                } else {
                    Toast.makeText(LocationActivity.this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public static void requestPermission(Activity activity, final int code){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},code);
        }
    }


    public static boolean checkPermission(Activity activity){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static class ErrorDialogFragment extends DialogFragment {
        public Dialog onCreateDialog(Bundle bundle) {
            return GooglePlayServicesUtil.getErrorDialog(getArguments().getInt(LocationActivity.DIALOG_ERROR), getActivity(), 1001);
        }

        public void onDismiss(DialogInterface dialogInterface) {
            super.onDismiss(dialogInterface);
            ((LocationActivity) getActivity()).onDialogDismissed();
        }
    }

    public void onConnectionSuspended(int i) {
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(102);
    }

    private boolean isGooglePlayServicesAvailable() {
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.d(TAG, "onStart fired .google play .............");
        if (isGooglePlayServicesAvailable == 0) {
            return true;
        }
        GooglePlayServicesUtil.getErrorDialog(isGooglePlayServicesAvailable, this, 0).show();
        return false;
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) "GPS is disabled. Would you like to enable it?").setCancelable(false).setPositiveButton((CharSequence) "Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        });
        alertDialogBuilder.setNegativeButton((CharSequence) "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
    }

    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
        Log.d(TAG, "Location update stopped .......................");
    }


    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
        if (locationManager.isProviderEnabled("gps")) {
            latitude = (TextView) findViewById(R.id.location_latitude);
            longitude = (TextView) findViewById(R.id.location_longitude);
            city = (TextView) findViewById(R.id.location_city);
            state = (TextView) findViewById(R.id.location_state);
            country = (TextView) findViewById(R.id.location_country);
            return;
        }
        showGPSDisabledAlertToUser();
    }

    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
            Log.d(TAG, "Location update started ..............: ");
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    public List<Address> getGeocoderAddress(Context context) {
        if (mCurrentLocation != null) {
            try {
                geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), geocoderMaxResults);
                Log.d(TAG, "Address in Geocoder" + addresses);
                return addresses;
            } catch (IOException e) {
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }
        return null;
    }

    public String getAddressLine(Context context) {
        if (addresses == null || addresses.size() <= 0) {
            return null;
        }
        Address address = (Address) addresses.get(0);
        LinkedHashMap<String, String> getAddressLineMap = new LinkedHashMap();
        getAddressLineMap.put("Address Line 0", address.getAddressLine(0));
        getAddressLineMap.put("Address Line 1", address.getAddressLine(1));
        getAddressLineMap.put("Address Line 2", address.getAddressLine(2));
        getAddressLineMap.put("Address Line 3", address.getAddressLine(3));
        return "" + ((String) getAddressLineMap.get("Address Line 1")) + "\n" + ((String) getAddressLineMap.get("Address Line 2")) + " [" + ((String) getAddressLineMap.get("Address Line 3")) + "].";
    }

    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses == null || addresses.size() <= 0) {
            return null;
        }
        return ((Address) addresses.get(0)).getLocality();
    }

    public String getState(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses == null || addresses.size() <= 0) {
            return null;
        }
        return ((Address) addresses.get(0)).getAdminArea();
    }

    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses == null || addresses.size() <= 0) {
            return null;
        }
        return ((Address) addresses.get(0)).getCountryName();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (mCurrentLocation != null) {
            String stringLatitude = String.valueOf(mCurrentLocation.getLatitude());
            String stringLongitude = String.valueOf(mCurrentLocation.getLongitude());
            addresses = getGeocoderAddress(getApplicationContext());
            if (addresses != null && addresses.size() > 0) {
                Address address = (Address) addresses.get(0);
                String stringcity = address.getLocality();
                String stringcountry = address.getCountryName();
                String stringstate = address.getAdminArea();
                if (!(latitude == null || longitude == null || city == null || country == null || state == null )) {
                    latitude.setText(stringLatitude);
                    longitude.setText(stringLongitude);
                    country.setText(stringcountry);
                    state.setText(stringstate);
                    city.setText(stringcity);

                }
            }
            currentLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    public void onDialogDismissed() {
        mResolvingError = false;
    }



}
