package com.familylocation.mobiletracker.phoneToolsModule;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.familylocation.mobiletracker.BaseActivity;
import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;

import android.support.v4.media.session.PlaybackStateCompat;

public class SystemUseActivity extends BaseActivity {
    TextView ramTotal,ramFree,ramUsed,isiTotal,isiFree,esiTotal,esiFree,batteryLevel,batteryStatus,batteryHealth, batteryTemperature,batteryTech,batteryVoltage,batteryUptime;
    public long free = 0;
    public long total = 0;
    private static final String ERROR = null;
    Intent batteryIntent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sytem_use);

        removeStatusBar();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);

        RelativeLayout rl_adplaceholder= findViewById(R.id.rl_adplaceholder);
        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, SystemUseActivity.this);


        batteryIntent = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        isitElement();
        updateInfo();
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



    private void isitElement() {
        ramTotal = findViewById(R.id.system_ram_total);
        ramFree = findViewById(R.id.system_ram_free);
        ramUsed = findViewById(R.id.system_ram_used);
        isiTotal = findViewById(R.id.system_isi_total);
        isiFree = findViewById(R.id.system_isi_free);
        esiTotal = findViewById(R.id.system_esi_total);
        esiFree =  findViewById(R.id.system_esi_free);
        batteryLevel = findViewById(R.id.system_battery_level);
        batteryStatus = findViewById(R.id.system_battery_status);
        batteryHealth = findViewById(R.id.system_battery_health);
        batteryTemperature = findViewById(R.id.system_battery_temp);
        batteryTech = findViewById(R.id.system_battery_tech);
        batteryVoltage = findViewById(R.id.system_battery_voltage);
        batteryUptime = findViewById(R.id.system_battery_uptime);
    }

    public void updateInfo() {
        getMemorySize();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        if (this.ramTotal != null) {
            this.ramTotal.setText(calSize((double) this.total));
        }
        if (this.ramFree != null) {
            this.ramFree.setText(calSize((double) this.free));
        }
        if (this.ramUsed != null) {
            this.ramUsed.setText(calSize((double) (this.total - this.free)));
        }
        if (this.isiTotal != null) {
            this.isiTotal.setText(getTotalInternalMemorySize());
        }
        if (this.isiFree != null) {
            this.isiFree.setText(getAvailableInternalMemorySize());
        }
        if (this.esiFree != null) {
            this.esiFree.setText(getAvailableExternalMemorySize());
        }
        if (this.esiTotal != null) {
            this.esiTotal.setText(getTotalExternalMemorySize());
        }
        if (this.batteryLevel != null) {
            this.batteryLevel.setText(String.valueOf(batteryLevel()));
        }
        if (this.batteryStatus != null) {
            this.batteryStatus.setText(batteryStatus());
        }
        if (this.batteryTech != null) {
            this.batteryTech.setText(batteryTech());
        }
        if (this.batteryVoltage != null) {
            this.batteryVoltage.setText(batteryVolt());
        }
        if (this.batteryTemperature != null) {
            this.batteryTemperature.setText(batteryTemp());
        }
        if (this.batteryHealth != null) {
            this.batteryHealth.setText(batteryHealth());
        }
        if (this.batteryUptime != null) {
            this.batteryUptime.setText(uptimeMillis());
        }
    }

    private void getMemorySize() {
        Pattern compile = Pattern.compile("([a-zA-Z]+):\\s*(\\d+)");
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("/proc/meminfo", "r");
            while (true) {
                CharSequence readLine = randomAccessFile.readLine();
                if (readLine != null) {
                    Matcher matcher = compile.matcher(readLine);
                    if (matcher.find()) {
                        String group = matcher.group(1);
                        String group2 = matcher.group(2);
                        if (group.equalsIgnoreCase("MemTotal")) {
                            this.total = Long.parseLong(group2);
                        } else if (group.equalsIgnoreCase("MemFree") || group.equalsIgnoreCase("SwapFree")) {
                            this.free = Long.parseLong(group2);
                        }
                    }
                } else {
                    randomAccessFile.close();
                    this.total *= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                    this.free *= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String calSize(double d) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double d2 = d / 1048576.0d;
        double d3 = d / 1.073741824E9d;
        double d4 = d / 1.099511627776E12d;
        if (d4 > 1.0d) {
            return decimalFormat.format(d4).concat(" TB");
        }
        if (d3 > 1.0d) {
            return decimalFormat.format(d3).concat(" GB");
        }
        if (d2 > 1.0d) {
            return decimalFormat.format(d2).concat(" MB");
        }
        return decimalFormat.format(d).concat(" KB");
    }

    public static String getTotalInternalMemorySize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return formatSize((double) (((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize())));
    }

    public static String getAvailableInternalMemorySize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return formatSize((double) (((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())));
    }

    public static String getAvailableExternalMemorySize() {
        if (!externalMemoryAvailable()) {
            return ERROR;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return formatSize((double) (((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())));
    }

    public static String getTotalExternalMemorySize() {
        if (!externalMemoryAvailable()) {
            return ERROR;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return formatSize((double) (((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize())));
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static String formatSize(double d) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double d2 = d / 1048576.0d;
        double d3 = d / 1.073741824E9d;
        double d4 = d / 1.099511627776E12d;
        if (d4 > 1.0d) {
            return decimalFormat.format(d4).concat(" TB");
        }
        if (d3 > 1.0d) {
            return decimalFormat.format(d3).concat(" GB");
        }
        if (d2 > 1.0d) {
            return decimalFormat.format(d2).concat(" MB");
        }
        return decimalFormat.format(d).concat(" KB");
    }





    //  Battery start hERE

    public float batteryLevel() {
        int intExtra = this.batteryIntent.getIntExtra("level", -1);
        int intExtra2 = this.batteryIntent.getIntExtra("scale", -1);
        if (intExtra == -1 || intExtra2 == -1) {
            return 50.0f;
        }
        //this.pb.setProgress(intExtra);
        return (((float) intExtra) / ((float) intExtra2)) * 100.0f;
    }

    public String batteryStatus() {
        int intExtra = this.batteryIntent.getIntExtra("plugged", -1);
        if (intExtra == 4) {
            return "WIRELESS Charging";
        }
        switch (intExtra) {
            case 1:
                return "AC Charging";
            case 2:
                return "USB Charging";
            default:
                return "NOT Charging";
        }
    }

    public String batteryTech() {
        return this.batteryIntent.getExtras().getString("technology");
    }

    public String batteryHealth() {
        int intExtra = this.batteryIntent.getIntExtra("health", 1);
        if (intExtra == 2) {
            return "Good";
        }
        if (intExtra == 3) {
            return "Over Heat";
        }
        if (intExtra == 4) {
            return "Dead";
        }
        if (intExtra == 5) {
            return "Over Voltage";
        }
        return intExtra == 6 ? "Unspecified Failure" : "Unknown";
    }

    public String batteryVolt() {
        return String.valueOf(this.batteryIntent.getIntExtra("voltage", -1));
    }

    public String batteryTemp() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(((float) this.batteryIntent.getIntExtra("temperature", -1)) / 10.0f));
        stringBuilder.append(" *C");
        return stringBuilder.toString();
    }

    private String uptimeMillis() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        StringBuffer stringBuffer = new StringBuffer("");
        if (elapsedRealtime > 86400000) {
            stringBuffer.append(elapsedRealtime / 86400000);
            stringBuffer.append(" days ");
            elapsedRealtime %= 86400000;
        }
        if (elapsedRealtime > 3600000) {
            stringBuffer.append(elapsedRealtime / 3600000);
            stringBuffer.append(" hours ");
            elapsedRealtime %= 3600000;
        }
        if (elapsedRealtime > 60000) {
            stringBuffer.append(elapsedRealtime / 60000);
            stringBuffer.append(" min. ");
            elapsedRealtime %= 60000;
        }
        if (elapsedRealtime > 1000) {
            stringBuffer.append(elapsedRealtime / 1000);
            stringBuffer.append(" sec.");
            elapsedRealtime %= 1000;
        }
        return stringBuffer.toString();
    }
}
