package com.familylocation.mobiletracker.callSettingsModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.familylocation.mobiletracker.databinding.ItemBlockedNumberBinding


class BlockedNumbersAdapter(
    private var context: Context,
    private var blockedNumbers: List<String>,
    private val onUnBlockClicked: (blockedNumber: String) -> Unit,
) :
    RecyclerView.Adapter<BlockedNumbersAdapter.BlockedNumbersAdapterViewHolder>() {


    class BlockedNumbersAdapterViewHolder(var binding: ItemBlockedNumberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BlockedNumbersAdapterViewHolder {
        return BlockedNumbersAdapterViewHolder(
            ItemBlockedNumberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BlockedNumbersAdapterViewHolder, position: Int) {

        val blockedNumberItem = blockedNumbers[position]

        holder.binding.tvBLPhoneNumber.text = blockedNumberItem

        holder.binding.btUnblock.setOnClickListener {
            onUnBlockClicked.invoke(blockedNumberItem)
        }


    }


    override fun getItemCount(): Int {
        return blockedNumbers.size
    }

}