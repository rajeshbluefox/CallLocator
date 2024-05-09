package realapps.live.callerlocator.bankInformationModule.model;


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

import realapps.live.callerlocator.R;
import realapps.live.callerlocator.bankInformationModule.BankViewActivity;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    Context mContext;
    ArrayList<BankModel> mListData;

    public BankAdapter(Context mContext, ArrayList<BankModel> mListData) {
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
        final String title = mListData.get(i).getTitle();
        final String balance = mListData.get(i).getBalance();
        final String customer = mListData.get(i).getCustomer();
        final int img = mListData.get(i).getImg();
        holder.bankViewTitle.setText(title);
        holder.bankViewImg.setImageResource(img);
        holder.bankViewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.BankInfo_ringtone_click = Utils.BankInfo_ringtone_click + 1;

                Intent mIntent = new Intent(mContext, BankViewActivity.class);
                mIntent.putExtra("title", title);
                mIntent.putExtra("balance", balance);
                mIntent.putExtra("customer", customer);
                mIntent.putExtra("img", img);
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
