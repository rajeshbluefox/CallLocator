package com.familylocation.mobiletracker.simInfoModule.model;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.familylocation.mobiletracker.R;
import com.familylocation.mobiletracker.simInfoModule.NomInfomationActivity;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.BankViewHolder> {

    Context mContext;
    ArrayList<NumberModel> mListData;

    public NumberAdapter(Context mContext, ArrayList<NumberModel> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_bank_card, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, final int i) {
        final int img = mListData.get(i).getImg();
        holder.bankViewTitle.setText(mListData.get(i).getCardN_name());

        holder.bankViewImg.setImageResource(img);
        holder.bankViewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NumberModel mNumber=mListData.get(i);
                Intent mIntent = new Intent(mContext, NomInfomationActivity.class);
                mIntent.putExtra("mNumberData",mNumber);
                mContext.startActivity(mIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public static class BankViewHolder extends RecyclerView.ViewHolder {

        CardView bankViewCard;
        TextView bankViewTitle;
        ImageView bankViewImg;

        public BankViewHolder(@NonNull View view) {
            super(view);

            bankViewCard = view.findViewById(R.id.bank_view_card);
            bankViewImg = view.findViewById(R.id.bank_view_img);
            bankViewTitle = view.findViewById(R.id.bank_view_title);
        }
    }


}