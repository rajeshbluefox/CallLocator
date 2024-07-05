package com.familylocation.mobiletracker.bankInformationModule;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.familylocation.mobiletracker.BaseActivity;
import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;


public class BankViewActivity extends BaseActivity {

    String title,balance,customer;
    int img;

    TextView balanceNumber;
    TextView customerNumber;
    CardView balanceCard, customerCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_view);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        RelativeLayout rl_adplaceholder= findViewById(R.id.rl_adplaceholder);
//        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, BankViewActivity.this);

        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
        loadBannerAds(rel_banner);

        removeStatusBar();

        title = getIntent().getStringExtra("title");
        ( (TextView)findViewById(R.id.titleBar)).setText(title);
        balance = getIntent().getStringExtra("balance");
        customer = getIntent().getStringExtra("customer");
        img = getIntent().getIntExtra("img",0);

        balanceNumber = findViewById(R.id.check_balance_number);
        customerNumber =findViewById(R.id.customer_care_number);
        balanceCard = findViewById(R.id.check_balance_card);
        customerCard =findViewById(R.id.customer_care_card);

        balanceNumber.setText(balance);
        customerNumber.setText(customer);

        balanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + balance)));
            }
        });

        customerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customer)));
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
}
