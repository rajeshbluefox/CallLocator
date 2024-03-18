package realapps.live.callerlocator.callLocatorModule

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.callLocatorModule.apiFunctions.CallLocatorApiFunctions
import realapps.live.callerlocator.callLocatorModule.apiFunctions.CallLocatorViewModel
import realapps.live.callerlocator.callLocatorModule.modalClass.GetFriendRequestDataItem
import realapps.live.callerlocator.callLocatorModule.supportFunctions.FriendRequestAdapter
import realapps.live.callerlocator.databinding.ActivityFriendRequestBinding
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions
import realapps.live.callerlocator.zSharedPreference.LoginData

@AndroidEntryPoint
class FriendRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendRequestBinding

    private lateinit var callLocatorViewModel: CallLocatorViewModel

    private lateinit var callLocatorApiFunctions: CallLocatorApiFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initTabs()
    }

    private fun initViews() {
        callLocatorViewModel = ViewModelProvider(this)[CallLocatorViewModel::class.java]

        callLocatorApiFunctions = CallLocatorApiFunctions(
            this, this,
            this,
            callLocatorViewModel,
            sendFriendRequestObserverCB = {},
            ::getFriendRequestsResponse
        )
    }

    private fun initTabs() {

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Received"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Sent"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position

                    SelectedTab.selectedTab = position

                    getFriendRequests(position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })
    }

    fun getFriendRequests(position: Int) {
        if (position == 0) {
            if (receivedFriendReqList.size == 0)
                callLocatorApiFunctions.getFriendRequests(
                    LoginData.getUserPhone(this),
                    "0"
                )
            else {
                initRv()
            }
        } else {
            if (sentFriendReqList.size == 0)
                callLocatorApiFunctions.getFriendRequests(
                    LoginData.getUserPhone(this),
                    position.toString()
                )
            else {
                initRv()
            }
        }
    }

    fun initRv()
    {

        val requestList: ArrayList<GetFriendRequestDataItem> = if(SelectedTab.selectedTab==0)
            receivedFriendReqList
        else
            sentFriendReqList

        if(requestList.size!=0)
        {

        val friendRequestAdapter = FriendRequestAdapter(this, requestList, onItemClicked = {})
        binding.friendRequestsRv.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = friendRequestAdapter
        }
        }else{
            binding.friendRequestsRv.visibility=View.GONE
            UtilFunctions.showToast(this,"No Items Found")
        }
    }

    private var sentFriendReqList = ArrayList<GetFriendRequestDataItem>()
    private var receivedFriendReqList = ArrayList<GetFriendRequestDataItem>()

    private fun getFriendRequestsResponse(requestList: List<GetFriendRequestDataItem?>?) {
        if (SelectedTab.selectedTab == 0)
            sentFriendReqList = requestList as ArrayList<GetFriendRequestDataItem>
        else
            receivedFriendReqList = requestList as ArrayList<GetFriendRequestDataItem>
    }

    object SelectedTab {
        var selectedTab = 0
    }

}