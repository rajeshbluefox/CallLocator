package com.familylocation.mobiletracker.callLocatorModule.supportFunctions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.callLocatorModule.modalClass.MyFriendsDataItem
import com.familylocation.mobiletracker.databinding.ItemMyfriendsBinding
import java.text.SimpleDateFormat
import java.util.Locale


class MyFriendsAdapter(
    private var context: Context,
    private var requestsList: ArrayList<MyFriendsDataItem?>,
    private val onItemClicked: (myFriendsDataItem: MyFriendsDataItem) -> Unit,
) :
    RecyclerView.Adapter<MyFriendsAdapter.MyFriendsAdapterViewHolder>() {

    private var preSel = -1
    private var curSel = -1

    fun resetValues()
    {
        preSel=-1
        curSel=-1
    }
    class MyFriendsAdapterViewHolder(var binding: ItemMyfriendsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFriendsAdapterViewHolder {
        return MyFriendsAdapterViewHolder(
            ItemMyfriendsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: MyFriendsAdapterViewHolder, position: Int) {

        val request = requestsList[position]

        if (request != null) {

            if(curSel==position)
            {
                holder.binding.itemFriend.setBackgroundColor(context.resources.getColor(R.color.bg_color_3))
                holder.binding.ivUser.setImageResource(R.drawable.user_sel)
                holder.binding.ivTimeStamp.setImageResource(R.drawable.clock_sel)

                holder.binding.selTick.visibility=View.VISIBLE

                holder.binding.tvContactName.setTextColor(context.resources.getColor(R.color.white))
                holder.binding.tvPhoneNumber.setTextColor(context.resources.getColor(R.color.white))

                holder.binding.tvTimeStamp.setTextColor(context.resources.getColor(R.color.bg_color_3))
            }else
            {
                holder.binding.itemFriend.setBackgroundColor(context.resources.getColor(R.color.bg_color_1))
                holder.binding.ivUser.setImageResource(R.drawable.user1)
                holder.binding.ivTimeStamp.setImageResource(R.drawable.baseline_access_time_filled_24)

                holder.binding.selTick.visibility=View.GONE

                holder.binding.tvContactName.setTextColor(context.resources.getColor(R.color.bg_color_2))
                holder.binding.tvPhoneNumber.setTextColor(context.resources.getColor(R.color.bg_color_2))

                holder.binding.tvTimeStamp.setTextColor(context.resources.getColor(R.color.bg_color_2))

            }

            if (request.friendName != null)
                holder.binding.tvContactName.text = request.friendName
            else
                holder.binding.tvContactName.text = "UnKnown"

            holder.binding.tvPhoneNumber.text = request.friendNumber

            holder.binding.tvTimeStamp.text = convertTime(request.locationTimeStamp!!)
        }

        holder.binding.itemFriend.setOnClickListener {
            onItemClicked.invoke(request!!)

//            notifyItemChanged(position)
            preSel=curSel
            curSel=position

            SelectedFriend.selectedFriendNo=curSel

            notifyItemChanged(curSel)

            if(preSel!=-1)
                notifyItemChanged(preSel)


        }

    }

    override fun getItemCount(): Int {
        return requestsList.size
    }

    private fun convertTime(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a , dd-MM-yy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)

        return outputFormat.format(date)
    }

}