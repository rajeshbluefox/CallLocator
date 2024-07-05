package com.familylocation.mobiletracker.simInfoModule;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.simInfoModule.model.NumberModel;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;


public class NomInfomationActivity extends AppCompatActivity {


    TextView tv_main_balance_enquiry;
    TextView tv_how_to_recharge;
    TextView tv_msg_balance;
    TextView tv_net_balance;
    TextView tv_CC_number;
    TextView tv_your_number;
    CardView how_to_recharge, main_balance, msg_balance, net_balance, your_number, CC_number;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nom_infomation);
        removeStatusBar();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        tv_main_balance_enquiry = findViewById(R.id.tv_main_balance_enquiry);
        tv_how_to_recharge = findViewById(R.id.tv_how_to_recharge);
        tv_your_number = findViewById(R.id.tv_your_number);
        tv_msg_balance = findViewById(R.id.tv_msg_balance);
        tv_net_balance = findViewById(R.id.tv_net_balance);
        how_to_recharge = findViewById(R.id.how_to_recharge);
        tv_CC_number = findViewById(R.id.tv_CC_number);
        main_balance = findViewById(R.id.main_balance);
        msg_balance = findViewById(R.id.msg_balance);
        net_balance = findViewById(R.id.net_balance);
        your_number = findViewById(R.id.your_number);
        CC_number = findViewById(R.id.CC_number);

//        RelativeLayout rl_adplaceholder = findViewById(R.id.rl_adplaceholder);
//        MyApplication.getInstance().loadNativeAd(rl_adplaceholder, NomInfomationActivity.this);

        NumberModel mNumber = (NumberModel) getIntent().getSerializableExtra("mNumberData");
        String card_name = mNumber.getCardN_name();
        String mHow_to_recharge = mNumber.getRecharge();
        String mMain_balance = mNumber.getMain_balance();
        String mMsg_balance = mNumber.getMsg_balance();
        String mNet_balance = mNumber.getNet_balance();
        String mYour_number = mNumber.getYour_number();
        String mCC_number = mNumber.getCc_number();


        ((TextView) findViewById(R.id.titleBar)).setText(card_name);
        tv_how_to_recharge.setText(mHow_to_recharge);
        tv_main_balance_enquiry.setText(mMain_balance);
        tv_msg_balance.setText(mMsg_balance);
        tv_net_balance.setText(mNet_balance);
        tv_your_number.setText(mYour_number);
        tv_CC_number.setText(mCC_number);

        how_to_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mMain_balance)));
            }
        });

        main_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mHow_to_recharge)));
            }
        });
        msg_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mMsg_balance)));
            }
        });
        net_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mNet_balance)));
            }
        });
        your_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mYour_number)));
            }
        });
        CC_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mCC_number)));
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
