package com.familylocation.mobiletracker.callLocatorModule.supportFunctions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.familylocation.mobiletracker.callLocatorModule.FriendRequestActivity
import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetFriendRequestDataItem
import com.familylocation.mobiletracker.databinding.ItemFriendRequestBinding


class FriendRequestAdapter(
    private var context: Context,
    private var requestsList: ArrayList<GetFriendRequestDataItem>,
    private val onItemClicked: (getFriendRequestDataItem: GetFriendRequestDataItem,clickType: Int) -> Unit,
) :
    RecyclerView.Adapter<FriendRequestAdapter.FriendRequestAdapterViewHolder>() {


    class FriendRequestAdapterViewHolder(var binding: ItemFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRequestAdapterViewHolder {
        return FriendRequestAdapterViewHolder(
            ItemFriendRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: FriendRequestAdapterViewHolder, position: Int) {

        val request = requestsList[position]

        if (FriendRequestActivity.SelectedTab.selectedTab == 0) {
//            holder.binding.content.text = "You have received request"
            holder.binding.numberValue.text = request.fromNumber

            if (request.requestStatus == "0") {
                holder.binding.btAccept.visibility = View.VISIBLE
                holder.binding.tvSentAccepted.visibility = View.GONE
            } else if (request.requestStatus == "1") {
                holder.binding.btAccept.visibility = View.GONE
                holder.binding.tvSentAccepted.visibility = View.VISIBLE
                holder.binding.tvSentAccepted.text = "Accepted"

                holder.binding.ivDelete.visibility=View.VISIBLE
            } else {
                holder.binding.btAccept.visibility = View.GONE
                holder.binding.tvSentAccepted.visibility = View.VISIBLE
                holder.binding.tvSentAccepted.text = "Rejected"
            }
        } else {
//            holder.binding.content.text = "You have sent request"
            holder.binding.numberValue.text = request.toNumber

            if (request.requestStatus == "0") {
                holder.binding.btAccept.visibility = View.GONE
                holder.binding.tvSentAccepted.visibility = View.VISIBLE
                holder.binding.tvSentAccepted.text = "Pending"
            } else if (request.requestStatus == "1") {
                holder.binding.btAccept.visibility = View.GONE
                holder.binding.tvSentAccepted.visibility = View.VISIBLE
                holder.binding.tvSentAccepted.text = "Accepted"

                holder.binding.ivDelete.visibility=View.VISIBLE
            } else {
                holder.binding.btAccept.visibility = View.GONE
                holder.binding.tvSentAccepted.visibility = View.VISIBLE
                holder.binding.tvSentAccepted.text = "Rejected"
            }
        }

        holder.binding.btAccept.setOnClickListener {
            onItemClicked.invoke(request,1)
        }

        holder.binding.ivDelete.setOnClickListener {
            onItemClicked.invoke(request,2)
        }

    }

    override fun getItemCount(): Int {
        return requestsList.size
    }

}