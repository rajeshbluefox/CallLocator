package com.familylocation.mobiletracker.phoneToolsModule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

import com.familylocation.mobiletracker.BaseActivity;
import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;


public class AudioControlActivity extends BaseActivity {

    CardView setRingtone, setNotificationTone, setAlarmTone;
    CheckBox setVibrate;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_control);

        removeStatusBar();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);

        RelativeLayout rl_adplaceholder = findViewById(R.id.rl_adplaceholder);
        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, AudioControlActivity.this);


        setRingtone = findViewById(R.id.setringtone_card);
        setNotificationTone = findViewById(R.id.notification_card);
        setAlarmTone = findViewById(R.id.alarm_card);
        setVibrate = findViewById(R.id.vibrate_checkbox);
        sp = getSharedPreferences("vibrate_when_ringing", 0);
        ed = this.sp.edit();
        onClickListeners();
//        setRingtone.setOnClickListener(mListener);
//        setNotificationTone.setOnClickListener(mListener);
//        setAlarmTone.setOnClickListener(mListener);
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

    public void onClickListeners()
    {
        setRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(AudioControlActivity.this, 1);
                Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                intent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                intent.putExtra("android.intent.extra.ringtone.TITLE", "Select Ringtone");
                intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentTone);
                intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
                AudioControlActivity.this.startActivityForResult(intent, 1001);

            }
        });

        setNotificationTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paramAnonymousView = new Intent("android.intent.action.RINGTONE_PICKER");
                paramAnonymousView.putExtra("android.intent.extra.ringtone.TYPE", 2);
                paramAnonymousView.putExtra("android.intent.extra.ringtone.TITLE", "Select Notification Tone");
                paramAnonymousView.putExtra("android.intent.extra.ringtone.EXISTING_URI", RingtoneManager.getActualDefaultRingtoneUri(AudioControlActivity.this, 2));
                AudioControlActivity.this.startActivityForResult(paramAnonymousView, 1002);

            }
        });

        setAlarmTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarmIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                alarmIntent.putExtra("android.intent.extra.ringtone.TYPE", 4);
                alarmIntent.putExtra("android.intent.extra.ringtone.TITLE", "Select Alarm Tone");
                alarmIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", RingtoneManager.getActualDefaultRingtoneUri(AudioControlActivity.this, 4));
                AudioControlActivity.this.startActivityForResult(alarmIntent, 1003);

            }
        });
    }

//    View.OnClickListener mListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.setringtone_card:
//                    Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(AudioControlActivity.this, 1);
//                    Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
//                    intent.putExtra("android.intent.extra.ringtone.TYPE", 1);
//                    intent.putExtra("android.intent.extra.ringtone.TITLE", "Select Ringtone");
//                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentTone);
//                    intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
//                    intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
//                    AudioControlActivity.this.startActivityForResult(intent, 1001);
//                    break;
//                case R.id.notification_card:
//                    Intent paramAnonymousView = new Intent("android.intent.action.RINGTONE_PICKER");
//                    paramAnonymousView.putExtra("android.intent.extra.ringtone.TYPE", 2);
//                    paramAnonymousView.putExtra("android.intent.extra.ringtone.TITLE", "Select Notification Tone");
//                    paramAnonymousView.putExtra("android.intent.extra.ringtone.EXISTING_URI", RingtoneManager.getActualDefaultRingtoneUri(AudioControlActivity.this, 2));
//                    AudioControlActivity.this.startActivityForResult(paramAnonymousView, 1002);
//                    break;
//                case R.id.alarm_card:
//                    Intent alarmIntent = new Intent("android.intent.action.RINGTONE_PICKER");
//                    alarmIntent.putExtra("android.intent.extra.ringtone.TYPE", 4);
//                    alarmIntent.putExtra("android.intent.extra.ringtone.TITLE", "Select Alarm Tone");
//                    alarmIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", RingtoneManager.getActualDefaultRingtoneUri(AudioControlActivity.this, 4));
//                    AudioControlActivity.this.startActivityForResult(alarmIntent, 1003);
//                    break;
//            }
//        }
//    };

}
