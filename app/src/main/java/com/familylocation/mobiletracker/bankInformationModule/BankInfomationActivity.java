package com.familylocation.mobiletracker.bankInformationModule;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familylocation.mobiletracker.BaseActivity;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;
import java.util.List;

import com.familylocation.mobiletracker.MyApplication;
import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.bankInformationModule.model.BankAdapter;
import com.familylocation.mobiletracker.bankInformationModule.model.BankModel;
import com.familylocation.mobiletracker.zCommonFuntions.JStatusBarUtils;

public class BankInfomationActivity extends BaseActivity {

    RecyclerView recyclerView;
    ArrayList<BankModel> mArrayList;
    BankAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bnk_infomation);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);

        removeStatusBar();

        NativeAd nativeAd = null;
        List<NativeAd> mNativeAdsGHome = MyApplication.getApplication().getGNativeHome();
        if (mNativeAdsGHome.size() > 0) {
            nativeAd = mNativeAdsGHome.get(0);
        }

//        RelativeLayout rel_banner = findViewById(R.id.rel_banner);
//        loadBannerAds(rel_banner);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        mArrayList = new ArrayList<>();
        mArrayList.add(new BankModel("City Bank", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Saraswat Bank", "9223040000", "1800229999", R.drawable.saraswat_bank));
        mArrayList.add(new BankModel("IDBI Bank", "18008431122", "18002001947", R.drawable.idbi_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("HDFC Bank", "18002703333", "18004254332", R.drawable.hdfc_bank));
        mArrayList.add(new BankModel("Bank Of Baroda", "09223011311", "18001024455", R.drawable.bank_of_baroda));
        mArrayList.add(new BankModel("State Bank of India", "09223766666", "18004253800", R.drawable.state_bank_of_india));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Axis Bank", "18004195959", "18002095577", R.drawable.axis_bank));
        mArrayList.add(new BankModel("Central Bank Of India", "09222250000", "18002001911", R.drawable.central_bank_of_india));
        mArrayList.add(new BankModel("Kotak Mahindra Bank", "18002740110", "18602662666", R.drawable.kotak_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Canara Bank", "09015483483", "18004250018", R.drawable.canara_bank));
        mArrayList.add(new BankModel("Yes Bank", "09840909000", "18002000", R.drawable.yes_bank));
        mArrayList.add(new BankModel("Union Bank Of India", "09278792787", "18001030123", R.drawable.union_bank_of_india));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Bank Of India", "09015135135", "1800220229", R.drawable.bank_of_india));
        mArrayList.add(new BankModel("UCO Bank", "09278792787", "18001030123", R.drawable.uco_bank));
        mArrayList.add(new BankModel("Dena Bank", "09289356677", "18002336427", R.drawable.dena_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("American Express", "1800446630", "1800446630", R.drawable.american_express));
        mArrayList.add(new BankModel("South Indian Bank", "09223008488", "18008431800", R.drawable.south_indian_bank));
        mArrayList.add(new BankModel("Vijaya Bank", "18002665555", "18004255885", R.drawable.vijaya_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Indian Bank", "09289592895", "180042500000", R.drawable.indian_bank));
        mArrayList.add(new BankModel("Federal Bank", "8431900900", "18004251199", R.drawable.federal_bank));
        mArrayList.add(new BankModel("Punjab National Bank", "18001802222", "18001802222", R.drawable.punjab_national_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("State Bank of Bikaner", "09223866666", "18001806005", R.drawable.state_bank_of_bikaner_and_jaipur));
        mArrayList.add(new BankModel("Indian Overseas Bank", "18004254445", "18004254445", R.drawable.indian_overseas_bank));
        mArrayList.add(new BankModel("HSBS Bank", "18001034722", "18001034722", R.drawable.hsbc_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Barclays Bank", "18002336565", "0442476842100", R.drawable.barclays_bank));
        mArrayList.add(new BankModel("ABN AMRO", "1800112224", "1800112224", R.drawable.abn_amro));
        mArrayList.add(new BankModel("Karur Vysya Bank", "09266292666", "18602001916", R.drawable.karur_vysya_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Allahabad Bank", "09224160150", "1800226061", R.drawable.allhabad_bank));
        mArrayList.add(new BankModel("Bank Of Maharashtra", "0922281818", "18002334526", R.drawable.bank_of_maharashtra));
        mArrayList.add(new BankModel("ANZ Bank", "18002000269", "18002000269", R.drawable.anz_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Cashnet Bank", "1800225087", "1800225087", R.drawable.cashnet_bank));
        mArrayList.add(new BankModel("Corporation Bank", "09268892688", "18004253555", R.drawable.corporation_bank));
        mArrayList.add(new BankModel("Punjab and Sind Bank", "1800221908", "1800221908", R.drawable.punjab_and_sind_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Andhra Bank", "09223011300", "18004251515", R.drawable.andhra_bank));
        mArrayList.add(new BankModel("Bharatiya Mahila Bank", "09212438888", "01147472100", R.drawable.bharatiya_mahila_bank));
        mArrayList.add(new BankModel("Centurion Bank Of Punjab", "1800443555", "18004253555", R.drawable.centurion_bank_of_punjab));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Karnataka Bank", "18004251445", "18004251444", R.drawable.karnataka_bank));
        mArrayList.add(new BankModel("Deutsche Bank", "18001236601", "18001236601", R.drawable.deutsche_bank));
        mArrayList.add(new BankModel("Standard Chartered Bank", "18003451212", "18003455000", R.drawable.standard_chartered_bank));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Dhanalakshmi Bank", "08067747700", "18004251747", R.drawable.dhanlaxmi_bank));
        mArrayList.add(new BankModel("United Bank Of India", "09015431345", "18003450345", R.drawable.united_bank_of_india));
        mArrayList.add(new BankModel("State Bank Of Travancore", "09223866666", "18004255566", R.drawable.state_bank_of_travancore));
        mArrayList.add(new BankModel("", "18004252484", "1800226747", R.drawable.citi_bank));
        mArrayList.add(new BankModel("Syndicate Bank", "09664552255", "08026639966", R.drawable.syndicate_bank));
        mAdapter = new BankAdapter(this, mArrayList,nativeAd);
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
