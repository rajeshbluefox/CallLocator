package com.familylocation.mobiletracker.callLocatorModule

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import com.familylocation.mobiletracker.BaseActivity
import com.familylocation.mobiletracker.MyApplication
import com.familylocation.mobiletracker.callLocatorModule.apiFunctions.CallLocatorApiFunctions
import com.familylocation.mobiletracker.callLocatorModule.apiFunctions.CallLocatorViewModel
import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetFriendRequestDataItem
import com.familylocation.mobiletracker.callLocatorModule.supportFunctions.FriendRequestAdapter
import com.familylocation.mobiletracker.databinding.ActivityFriendRequestBinding
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions
import com.familylocation.mobiletracker.zSharedPreference.LoginData

@AndroidEntryPoint
class FriendRequestActivity : BaseActivity() {

    private lateinit var binding: ActivityFriendRequestBinding

    private lateinit var callLocatorViewModel: CallLocatorViewModel

    private lateinit var callLocatorApiFunctions: CallLocatorApiFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources, binding.mainLayout)

//        loadBannerAds(binding.relBanner)
        MyApplication.getInstance().loadNativeAd(binding.rlAdplaceholder, this@FriendRequestActivity)


        initViews()
        initTabs()

        onClickListeners()
        getFriendRequests(0)
    }

    private fun onClickListeners() {
        binding.btBack.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        callLocatorViewModel = ViewModelProvider(this)[CallLocatorViewModel::class.java]

        callLocatorApiFunctions = CallLocatorApiFunctions(
            this, this,
            this,
            callLocatorViewModel,
            sendFriendRequestObserverCB = {},
            ::getFriendRequestsResponse,
            ::acceptFRResponse,
            getMyFriendsResponse = { _, _ -> }
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
        Log.e(
            "Test",
            "$position rec: ${receivedFriendReqList.size} , sent: ${sentFriendReqList.size}"
        )
//        receivedFriendReqList.clear()
//        sentFriendReqList.clear()

        if (position == 0) {

            binding.title1.text = "You have received Request"

            callLocatorApiFunctions.getFriendRequests(
                LoginData.getUserPhone(this),
                "0"
            )


        } else {
            Log.e("Test", "92")
            binding.title1.text = "Sent to:"

            callLocatorApiFunctions.getFriendRequests(
                LoginData.getUserPhone(this),
                "1"
            )

        }
    }

    fun initRv() {

        var requestList: ArrayList<GetFriendRequestDataItem> = if (SelectedTab.selectedTab == 0)
            receivedFriendReqList
        else
            sentFriendReqList

        Log.e("Test", "Size ${requestList.size}")

        if (requestList.size != 0) {

            requestList = ArrayList(requestList.reversed())
            binding.friendRequestsRv.visibility = View.VISIBLE


            val friendRequestAdapter = FriendRequestAdapter(this, requestList, ::onAgreeClicked)
            binding.friendRequestsRv.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL, false
                )
                adapter = friendRequestAdapter
            }
        } else {
            binding.friendRequestsRv.visibility = View.GONE
            UtilFunctions.showToast(this, "No Items Found")
        }
    }

    private fun onAgreeClicked(getFriendRequestDataItem: GetFriendRequestDataItem,clickType: Int) {
        if (SelectedTab.selectedTab == 0) {
            val fromNumber = getFriendRequestDataItem.fromNumber!!
            val toNumber = LoginData.getUserPhone(this)

            Log.e("Test","if - fn : $fromNumber - tn : $toNumber")

            callLocatorApiFunctions.respondToFriendRequest(fromNumber, toNumber, clickType.toString())
        } else {
            val fromNumber = LoginData.getUserPhone(this)
            val toNumber = getFriendRequestDataItem.toNumber!!

            Log.e("Test","else - fn : $fromNumber - tn : $toNumber")

            callLocatorApiFunctions.respondToFriendRequest(fromNumber, toNumber, clickType.toString())
        }
    }

    fun acceptFRResponse(status: Boolean) {
        if (status) {
            UtilFunctions.showToast(this, "Request accepted")
            getFriendRequests(SelectedTab.selectedTab)
        } else {
            UtilFunctions.showToast(this, "Request Failed")
        }
    }

    private var sentFriendReqList = ArrayList<GetFriendRequestDataItem>()
    private var receivedFriendReqList = ArrayList<GetFriendRequestDataItem>()

    private fun getFriendRequestsResponse(
        requestList: List<GetFriendRequestDataItem?>?,
        isListPresent: Boolean
    ) {
        if (isListPresent) {
            if (SelectedTab.selectedTab == 1)
                sentFriendReqList = requestList as ArrayList<GetFriendRequestDataItem>
            else
                receivedFriendReqList = requestList as ArrayList<GetFriendRequestDataItem>

            initRv()
        } else {
            binding.friendRequestsRv.visibility = View.GONE
            UtilFunctions.showToast(this, "No Items Found")
        }
    }

    object SelectedTab {
        var selectedTab = 0
    }

}