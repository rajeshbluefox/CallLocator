package com.familylocation.mobiletracker.phoneToolsModule;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;


import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.familylocation.mobiletracker.BaseActivity;
import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;

import org.apache.http.conn.util.InetAddressUtils;


public class PhoneInfomationActivity extends BaseActivity {
    TextView deviceName, deviceModel, deviceBrand, deviceProductCode, deviceIMEI;
    TextView screenResolution, screenDensity, refreshRate, screenSize;
    TextView frontCamera, backCamera;
    TextView androidVersion, apiLevel;
    TextView cpuCore, cpuFrequency, cpuInstruction;
    TextView networkType, ipAddress, macAddress;
    TextView operator, country, roaming, serviceState;

    private String phonestate;
    private TelephonyManager telephonyManager;
    private Display display;
    private DisplayMetrics dm;
    private double deviceSize;
    private DecimalFormat twoDecimalForm;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_infomation);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);

        RelativeLayout rl_adplaceholder= findViewById(R.id.rl_adplaceholder);
        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, PhoneInfomationActivity.this);


        removeStatusBar();

        initElement();
        telephonyManager = (TelephonyManager) getSystemService("phone");
        display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.dm);
        deviceSize = Math.sqrt((double) (((((float) this.dm.widthPixels) / this.dm.xdpi) * (((float) this.dm.widthPixels) / this.dm.xdpi)) + ((((float) this.dm.heightPixels) / this.dm.ydpi) * (((float) this.dm.heightPixels) / this.dm.ydpi))));
        twoDecimalForm = new DecimalFormat("#.##");

        telephonyManager = (TelephonyManager) getSystemService("phone");
        telephonyManager.listen(new PhoneStateListener() {
            public void onServiceStateChanged(ServiceState serviceStateTemp) {
                super.onServiceStateChanged(serviceStateTemp);
                if (serviceStateTemp.getRoaming()) {
                }
                switch (serviceStateTemp.getState()) {
                    case 0:
                        phonestate = "STATE_IN_SERVICE";
                        if (serviceState != null) {
                            serviceState.setText(phonestate);
                            return;
                        }
                        return;
                    case 1:
                        phonestate = "STATE_OUT_OF_SERVICE";
                        if (serviceState != null) {
                            serviceState.setText(phonestate);
                            return;
                        }
                        return;
                    case 2:
                        phonestate = "STATE_EMERGENCY_ONLY";
                        if (serviceState != null) {
                            serviceState.setText(phonestate);
                            return;
                        }
                        return;
                    case 3:
                        phonestate = "STATE_POWER_OFF";
                        if (serviceState != null) {
                            serviceState.setText(phonestate);
                            return;
                        }
                        return;
                    default:
                        phonestate = "Unknown";
                        if (serviceState != null) {
                            serviceState.setText(phonestate);
                            return;
                        }
                        return;
                }
            }
        }, 1);
    }

    private void initElement() {

        deviceName = findViewById(R.id.device_name);
        deviceModel = findViewById(R.id.device_model);
        deviceBrand = findViewById(R.id.device_brand);
        deviceProductCode = findViewById(R.id.device_productcode);
        deviceIMEI = findViewById(R.id.device_imeino);

        screenResolution = findViewById(R.id.device_screen_resolution);
        screenDensity = findViewById(R.id.device_screen_density);
        refreshRate = findViewById(R.id.device_screen_refresh_rate);
        screenSize = findViewById(R.id.device_screen_size);

        frontCamera = findViewById(R.id.device_camera_front);
        backCamera = findViewById(R.id.device_camera_back);

        androidVersion = findViewById(R.id.device_version);
        apiLevel = findViewById(R.id.device_apilevel);

        cpuCore = findViewById(R.id.device_cpu_core);
        cpuFrequency = findViewById(R.id.device_cpu_max_frequency);
        cpuInstruction = findViewById(R.id.device_cpu_instruction_set);

        networkType = findViewById(R.id.device_network_type);
        ipAddress = findViewById(R.id.device_ip_address);
        macAddress = findViewById(R.id.device_mac_address);

        operator = findViewById(R.id.device_operator);
        country = findViewById(R.id.device_country);
        roaming = findViewById(R.id.device_roaming);
        serviceState = findViewById(R.id.device_service_state);

    }


    @SuppressLint("MissingPermission")
    public void onResume() {
        super.onResume();
        if (this.deviceModel != null) {
            this.deviceModel.setText(Build.MODEL);
        }
        if (this.deviceName != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 111);
//            }
            this.deviceName.setText(getDeviceName());
        }
        if (this.deviceBrand != null) {
            this.deviceBrand.setText(Build.BRAND);
        }
        if (this.deviceProductCode != null) {
            this.deviceProductCode.setText(Build.PRODUCT);
        }
        if (this.deviceIMEI != null) {
            try {
                if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == 0) {
                    deviceIMEI.setText(this.telephonyManager.getDeviceId());

                } else {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.screenResolution != null) {
            this.screenResolution.setText(this.dm.widthPixels + "w X " + this.dm.heightPixels + "h");
        }
        if (this.screenDensity != null) {
            this.screenDensity.setText(getDensityInfo());
        }
        if (this.refreshRate != null) {
            this.refreshRate.setText(this.display.getRefreshRate() + "Hz");
        }
        if (this.screenSize != null) {
            this.screenSize.setText(this.twoDecimalForm.format(this.deviceSize) + "\"(" + this.twoDecimalForm.format(this.deviceSize * 2.54d) + " cm)");
        }
        if (this.androidVersion != null) {
            this.androidVersion.setText(Build.VERSION.RELEASE + "( " + getAndroidVersionName() + ")");
        }
        if (this.apiLevel != null) {
            this.apiLevel.setText(String.valueOf(Build.VERSION.SDK_INT));
        }
        new TaskActivityFront().execute(new String[0]);
        new TaskActivityBack().execute(new String[0]);

        if (this.cpuCore != null) {
            this.cpuCore.setText(String.valueOf(getNumCores()));
        }
        if (this.cpuInstruction != null) {
            this.cpuInstruction.setText(getInstructionSets());
        }
        String cpuMaxFreq = "";
        try {
            RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
            try {
                cpuMaxFreq = reader.readLine();
                reader.close();
                RandomAccessFile randomAccessFile = reader;
            } catch (Exception e) {
            }
        } catch (Exception e2) {
        }
        if (this.operator != null) {
            this.operator.setText(this.telephonyManager.getNetworkOperatorName());
        }
        if (this.country != null) {
            this.country.setText(this.telephonyManager.getNetworkCountryIso());
        }
        if (this.cpuFrequency != null) {
            this.cpuFrequency.setText(cpuMaxFreq + "Hz");
        }
        if (this.networkType != null) {
            this.networkType.setText(getPhoneType());
        }
        if (this.roaming != null) {
            this.roaming.setText(roamingState());
        }
        if (this.ipAddress != null) {
            this.ipAddress.setText(getIPAddress(true));
        }
        if (this.macAddress != null) {
            this.macAddress.setText(getMACAddress("wlan0"));
        }
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

    private String getInstructionSets() {
        String str2 = Build.CPU_ABI;
        String str1 = str2;
        if (Build.VERSION.SDK_INT < 8) {
            return str1;
        }
        str1 = str2;
        if (Build.CPU_ABI2 == null) {
            return str1;
        }
        str1 = str2;
        if (Build.CPU_ABI2.equals(EnvironmentCompat.MEDIA_UNKNOWN)) {
            return str1;
        }
        return str2 + ", " + Build.CPU_ABI2;
    }

    public String getMACAddress(String interfaceName) {
        try {
            Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            if (it.hasNext()) {
                byte[] mac;
                NetworkInterface intf = (NetworkInterface) it.next();
                if (interfaceName == null || intf.getName().equalsIgnoreCase(interfaceName)) {
                    mac = intf.getHardwareAddress();
                } else {
                    mac = intf.getHardwareAddress();
                }
                if (mac == null) {
                    return "No H/W";
                }
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++) {
                    buf.append(String.format("%02X:", new Object[]{Byte.valueOf(mac[idx])}));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception e) {
        }
        return "";
    }

    public String getIPAddress(boolean useIPv4) {
        try {
            Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            while (it.hasNext()) {
                Iterator it2 = Collections.list(((NetworkInterface) it.next()).getInetAddresses()).iterator();
                while (it2.hasNext()) {
                    InetAddress addr = (InetAddress) it2.next();
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = Formatter.formatIpAddress(addr.hashCode());
                        if (InetAddressUtils.isIPv4Address(sAddr)) {
                            return sAddr;
                        }
                        int delim = sAddr.indexOf(37);
                        if (delim >= 0) {
                            return sAddr.substring(0, delim);
                        }
                        return sAddr;
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    public String getPhoneName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        if (myDevice != null) {
            return myDevice.getName();
        }
        return String.valueOf(" ");
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 111: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.deviceName.setText(getPhoneName());
                } else {
                    Toast.makeText(this, "Please tack Bluetooth.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private String roamingState() {
        if (this.telephonyManager.isNetworkRoaming()) {
            return "In Roaming";
        }
        return "Not Roaming";
    }

    private String getPhoneType() {
        String strphoneType = "";
        switch (this.telephonyManager.getPhoneType()) {
            case 0:
                return "NONE";
            case 1:
                return "GSM";
            case 2:
                return "CDMA";
            default:
                return strphoneType;
        }
    }

    private String getAndroidVersionName() {
        int i = Build.VERSION.SDK_INT;
        if (i == 7) {
            return "Eclair";
        }
        if (i == 8) {
            return "Froyo";
        }
        if (i == 9 || i == 10) {
            return "Gingerbread";
        }
        if (i == 11 || i == 12 || i == 13) {
            return "Honeycomb";
        }
        if (i == 14 || i == 15) {
            return "IceCream Sandwich";
        }
        if (i == 16 || i == 17 || i == 18) {
            return "Jelly Bean";
        }
        if (i == 19) {
            return "KitKat";
        }
        if (i == 21 || i == 22) {
            return "Lollipop";
        }
        if (i == 23) {
            return "Marshmallow";
        }
        if (i == 24 || i == 25) {
            return "Naugat";
        }
        return null;
    }

    String getDensityInfo() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        if (localDisplayMetrics.densityDpi == 120) {
            return "120 dpi (Low)";
        }
        if (localDisplayMetrics.densityDpi == 160) {
            return "160 dpi (Medium)";
        }
        if (localDisplayMetrics.densityDpi == 240) {
            return "240 dpi (High)";
        }
        if (localDisplayMetrics.densityDpi == 320) {
            return "320 dpi (X High)";
        }
        if (localDisplayMetrics.densityDpi == 480) {
            return "480 dpi (XX High)";
        }
        if (localDisplayMetrics.densityDpi == 640) {
            return "640 dpi (XXX High)";
        }
        if (localDisplayMetrics.densityDpi == 213) {
            return "TV";
        }
        if (localDisplayMetrics.densityDpi == 400) {
            return "400 dpi";
        }
        return "Unknown";
    }

    public String getCameraMegaPixels(int val) {
        Camera camera = null;
        List<Camera.Size> sizes = null;
        double wid = 0.0d;
        double heigh = 0.0d;
        if (null == null) {
            try {
                camera = Camera.open(val);
                sizes = camera.getParameters().getSupportedPictureSizes();
            } catch (RuntimeException e) {
                Log.e("Camera Error..", e.getMessage());
            }
        }
        ArrayList<Integer> arrayListForWidth = new ArrayList();
        ArrayList<Integer> arrayListForHeight = new ArrayList();
        if (sizes != null) {
            for (int i = 0; i < sizes.size(); i++) {
                Camera.Size result = (Camera.Size) sizes.get(i);
                arrayListForWidth.add(Integer.valueOf(result.width));
                arrayListForHeight.add(Integer.valueOf(result.height));
            }
        }
        if (!(arrayListForWidth.size() == 0 || arrayListForHeight.size() == 0)) {
            System.out.println("max W :" + Collections.max(arrayListForWidth));
            System.out.println("max H :" + Collections.max(arrayListForHeight));
            wid = (double) ((Integer) Collections.max(arrayListForWidth)).intValue();
            heigh = (double) ((Integer) Collections.max(arrayListForHeight)).intValue();
        }
        Log.e("Camera size ", "Camera pixel " + calSize((wid * heigh) / 1024000.0d));
        if (camera != null) {
            camera.release();
        }
        return calSize((wid * heigh) / 1024000.0d);
    }

    public String calSize(double value) {
        String lastValue = String.valueOf(" ");
        return this.twoDecimalForm.format(value).concat(" Megapixel");
    }

    public class TaskActivityFront extends AsyncTask<String, Void, String> {
        String strfrontCamera;

        protected String doInBackground(String... params) {
            strfrontCamera = getCameraMegaPixels(1);
            return null;
        }

        protected void onPostExecute(String s) {
            if (frontCamera != null) {
                frontCamera.setText(this.strfrontCamera);
            }
        }
    }

    public class TaskActivityBack extends AsyncTask<String, Void, String> {
        String backCameraValue;

        protected String doInBackground(String... params) {
            backCameraValue = getCameraMegaPixels(0);
            return null;
        }

        protected void onPostExecute(String s) {
            if (backCamera != null) {
                backCamera.setText(this.backCameraValue);
            }
        }
    }

    private int getNumCores() {
        try {
            return new File("/sys/devices/system/cpu/").listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return Pattern.matches("cpu[0-9]+", pathname.getName());
                }
            }).length;
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

}
