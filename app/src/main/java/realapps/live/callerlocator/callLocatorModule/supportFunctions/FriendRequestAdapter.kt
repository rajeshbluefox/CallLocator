package realapps.live.callerlocator.callLocatorModule.supportFunctions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import realapps.live.callerlocator.callLocatorModule.FriendRequestActivity
import realapps.live.callerlocator.callLocatorModule.modalClass.GetFriendRequestDataItem
import realapps.live.callerlocator.databinding.ItemFriendRequestBinding


class FriendRequestAdapter(
    private var context: Context,
    private var requestsList: ArrayList<GetFriendRequestDataItem>,
    private val onItemClicked: (getFriendRequestDataItem: GetFriendRequestDataItem) -> Unit,
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
            holder.binding.title1.text = "Request From :"
            holder.binding.content.text = "You have received request"
            holder.binding.numberValue.text = request.fromNumber

            if (request.requestStatus == "0") {
                holder.binding.btAgree.visibility = View.VISIBLE
                holder.binding.requestStatus.visibility = View.GONE
            } else if (request.requestStatus == "1") {
                holder.binding.btAgree.visibility = View.GONE
                holder.binding.requestStatus.visibility = View.VISIBLE
                holder.binding.requestStatus.text = "Accepted"
            } else {
                holder.binding.btAgree.visibility = View.GONE
                holder.binding.requestStatus.visibility = View.VISIBLE
                holder.binding.requestStatus.text = "Rejected"
            }
        } else {
            holder.binding.title1.text = "Sent to :"
            holder.binding.content.text = "You have sent request"
            holder.binding.numberValue.text = request.toNumber

            if (request.requestStatus == "0") {
                holder.binding.btAgree.visibility = View.GONE
                holder.binding.requestStatus.visibility = View.VISIBLE
                holder.binding.requestStatus.text = "Pending"
            } else if (request.requestStatus == "1") {
                holder.binding.btAgree.visibility = View.GONE
                holder.binding.requestStatus.visibility = View.VISIBLE
                holder.binding.requestStatus.text = "Accepted"
            } else {
                holder.binding.btAgree.visibility = View.GONE
                holder.binding.requestStatus.visibility = View.VISIBLE
                holder.binding.requestStatus.text = "Rejected"
            }
        }

        holder.binding.btAgree.setOnClickListener {
            onItemClicked.invoke(request)
        }

    }

    override fun getItemCount(): Int {
        return requestsList.size
    }

}