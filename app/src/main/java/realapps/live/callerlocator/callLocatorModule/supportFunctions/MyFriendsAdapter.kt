package realapps.live.callerlocator.callLocatorModule.supportFunctions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import realapps.live.callerlocator.callLocatorModule.modalClass.MyFriendsDataItem
import realapps.live.callerlocator.databinding.ItemMyfriendsBinding
import java.text.SimpleDateFormat
import java.util.Locale


class MyFriendsAdapter(
    private var context: Context,
    private var requestsList: ArrayList<MyFriendsDataItem?>,
    private val onItemClicked: (myFriendsDataItem: MyFriendsDataItem) -> Unit,
) :
    RecyclerView.Adapter<MyFriendsAdapter.MyFriendsAdapterViewHolder>() {


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
            holder.binding.tvPhoneNumber.text=request.friendNumber

            holder.binding.tvTimeStamp.text=convertTime(request.locationTimeStamp!!)
        }

        holder.binding.itemFriend.setOnClickListener{
            onItemClicked.invoke(request!!)
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