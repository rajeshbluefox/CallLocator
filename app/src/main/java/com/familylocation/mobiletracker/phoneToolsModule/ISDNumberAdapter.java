package com.familylocation.mobiletracker.phoneToolsModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import com.familylocation.mobiletracker.R;

public class ISDNumberAdapter extends RecyclerView.Adapter<ISDNumberAdapter.BankViewHolder> {

    Context mContext;
    private List<NumLocatorModel> models = new ArrayList<>();

    public ISDNumberAdapter(Context mContext, List<NumLocatorModel> models) {
        this.mContext = mContext;
        this.models = models;
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_isd, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, final int i) {
        NumLocatorModel mModel=models.get(i);
        holder.tv_coutry_name.setText(mModel.getCoutryName());
        holder.tv_code.setText(mModel.getCode());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class BankViewHolder extends RecyclerView.ViewHolder {


        TextView tv_coutry_name,tv_code;


        public BankViewHolder(@NonNull View view) {
            super(view);

            tv_coutry_name = view.findViewById(R.id.tv_coutry_name);
            tv_code = view.findViewById(R.id.tv_code);

        }
    }

    public void updateList(List<NumLocatorModel> list){
        models = list;
        notifyDataSetChanged();
    }
}
