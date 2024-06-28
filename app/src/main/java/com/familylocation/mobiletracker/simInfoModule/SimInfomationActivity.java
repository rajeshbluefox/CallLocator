package com.familylocation.mobiletracker.simInfoModule;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import com.familylocation.mobiletracker.BaseActivity;
import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.simInfoModule.model.NumberAdapter;
import com.familylocation.mobiletracker.simInfoModule.model.NumberModel;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;


public class SimInfomationActivity extends BaseActivity {

    CheckBox inCallCheck, outCallCheck;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    RecyclerView recyclerView;
    ArrayList<NumberModel> mNumberListData = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_infomation);
        removeStatusBar();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        /*ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        Bitmap bitmapLocal = decodeSampledBitmapFromResource(getResources(), R.drawable.ic_background, 500, 500);
        ivBack.setImageBitmap(bitmapLocal);
*/

//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);

        RelativeLayout rl_adplaceholder = findViewById(R.id.rl_adplaceholder);
        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, SimInfomationActivity.this);

        addListData();
        sp = getSharedPreferences("call_setings", MODE_PRIVATE);
        ed = sp.edit();

        inCallCheck = findViewById(R.id.in_call_check);
        outCallCheck = findViewById(R.id.out_call_check);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        NumberAdapter mAdapter = new NumberAdapter(this, mNumberListData);
        recyclerView.setAdapter(mAdapter);

        inCallCheck.setChecked(sp.getBoolean("in_call_value", true));
        outCallCheck.setChecked(sp.getBoolean("out_call_value", true));


        inCallCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed.putBoolean("in_call_value", isChecked);
                ed.apply();
            }
        });

        outCallCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed.putBoolean("out_call_value", isChecked);
                ed.commit();
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

    private void addListData() {

        mNumberListData = new ArrayList<>();
        mNumberListData.add(new NumberModel("Airtel", "*120*(16 digit code)#", "*123#",
                "*555#", "*123*10# or *123*11#", "*121*9", "121 or 198",
                R.drawable.airtel));
        mNumberListData.add(new NumberModel("Jio", "*123*(16 digit code)#", "*333#",
                "*112# then press 3", "*333# then press 2", "*1#", "1800 889 9999",
                R.drawable.jioo));
        mNumberListData.add(new NumberModel("Aircel", "*124*(16 digit code)#", "*125#",
                "*111*5# and  *111*12#", "*123*1#", "*1#", "121 or 198",
                R.drawable.aircel));
        mNumberListData.add(new NumberModel("Idea", "*124*(16 digit code)#", "*111#",
                "*167*3#", "*125#", "*1#", "12345",
                R.drawable.idea));
        mNumberListData.add(new NumberModel("Vodafone", "*140*(16 digit code)#", "*145# or *146#",
                "*142#", "*111*6# or *123*6*2#", "*777*0", "198 or 9825098250",
                R.drawable.vodafone));
        mNumberListData.add(new NumberModel("Telenor", "*222*3*(16 digit code)#", "*222*2#",
                "*222*2#", "*123#", "*1#", "121 or 9059090590",
                R.drawable.uninor));
        mNumberListData.add(new NumberModel("Tata Docomo", "*135*2#(16 digit code)#", "*111#",
                "*111*1#", "*123*1#", "*580", "121",
                R.drawable.docomo));
        mNumberListData.add(new NumberModel("Bsnl", "*124*2*(16 digit code)#", "*123#",
                "*123*10#", "*124#", "*1#", "1503 or 1800-345-1500",
                R.drawable.bsnl));
    }
}
