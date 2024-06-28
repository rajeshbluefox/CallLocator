package com.familylocation.mobiletracker.nearByPlacesModule;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;

public class FindTraficActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public String[] place = {"delivery", "bus station", "train station", "airport", "restaurant", "bank", "",
            "atm", "hospital", "police", "lodging", "car repair", "gas station", "",
            "mosque", "hindu temple", "church", "jewelry store", "shopping mall", "bar", "",
            "spa", "beauty saloon", "amusement park", "aquarium", "park", "zoo", "",
            "cafe", "dry cleaning", "pharmacy", "bakery", "doctor", "veterinary care", "",
            "dentist", "gym", "book store", "bowling alley", "car rental", "car wash", "",
            "taxi stand", "parking", "art gallery", "electronics store", "casino", "convenience store", "",
            "school", "fire station", "lawyer", "department store", "library", "liquor store", "",
            "movie theater", "museum", "night club", "pet store", "stadium", "local government office", "",
            "groceries", "car dealers", "home garden store", "clothing stores"};

    public int[] icon = {R.drawable.ic_1_delivery, R.drawable.ic_2_bus, R.drawable.ic_3_train, R.drawable.ic_4_airplane, R.drawable.ic_6_restorant, R.drawable.ic_6_bank, R.drawable.ic_6_bank,
            R.drawable.ic_7_atm, R.drawable.ic_8_hospital, R.drawable.ic_9_police, R.drawable.ic_10_lodging, R.drawable.ic_11_car_repair, R.drawable.ic_12_gas, R.drawable.ic_12_gas,
            R.drawable.ic_13_mosque, R.drawable.ic_14_hindu, R.drawable.ic_15_church, R.drawable.ic_16_jewelry, R.drawable.ic_17_shopping_mall, R.drawable.ic_18_jacket, R.drawable.ic_18_jacket,
            R.drawable.ic_1_delivery, R.drawable.ic_2_bus, R.drawable.ic_3_train, R.drawable.ic_4_airplane, R.drawable.ic_7_atm, R.drawable.ic_6_restorant,
            R.drawable.ic_2_bus, R.drawable.ic_3_train, R.drawable.ic_4_airplane, R.drawable.ic_7_atm,
            R.drawable.ic_6_bank, R.drawable.ic_8_hospital, R.drawable.ic_9_police,
            R.drawable.ic_13_mosque, R.drawable.ic_14_hindu, R.drawable.ic_15_church, R.drawable.ic_16_jewelry, R.drawable.ic_17_shopping_mall, R.drawable.ic_18_jacket,
            R.drawable.ic_10_lodging, R.drawable.ic_11_car_repair, R.drawable.ic_12_gas,
            R.drawable.ic_1_delivery, R.drawable.ic_2_bus, R.drawable.ic_3_train,
            R.drawable.ic_4_airplane, R.drawable.ic_7_atm, R.drawable.ic_6_restorant,
            R.drawable.ic_6_bank, R.drawable.ic_8_hospital, R.drawable.ic_9_police, R.drawable.ic_10_lodging, R.drawable.ic_1_delivery, R.drawable.ic_2_bus, R.drawable.ic_3_train,
            R.drawable.ic_11_car_repair, R.drawable.ic_12_gas,
            R.drawable.ic_13_mosque, R.drawable.ic_14_hindu, R.drawable.ic_15_church, R.drawable.ic_16_jewelry, R.drawable.ic_17_shopping_mall, R.drawable.ic_18_jacket,
            R.drawable.ic_4_airplane, R.drawable.ic_7_atm, R.drawable.ic_6_restorant,
            R.drawable.ic_6_bank, R.drawable.ic_8_hospital, R.drawable.ic_9_police, R.drawable.ic_10_lodging, R.drawable.ic_11_car_repair, R.drawable.ic_12_gas,
            R.drawable.ic_1_delivery, R.drawable.ic_6_restorant,
            R.drawable.ic_6_bank, R.drawable.ic_8_hospital, R.drawable.ic_9_police, R.drawable.ic_11_car_repair, R.drawable.ic_12_gas};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trafic);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        removeStatusBar();

        recyclerView = findViewById(R.id.recyclerView);
//        NativeAd nativeAd = null;
//        List<NativeAd> mNativeAdsGHome = MyApplication.getApplication().getGNativeHome();
//        if (mNativeAdsGHome.size() > 0) {
//            nativeAd = mNativeAdsGHome.get(0);
//        }
        FindTraficAdapter mAdapter = new FindTraficAdapter(FindTraficActivity.this, place, icon);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case 1:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
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

}
