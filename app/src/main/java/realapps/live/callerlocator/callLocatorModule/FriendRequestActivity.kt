package realapps.live.callerlocator.callLocatorModule

import android.os.Bundle
import android.util.Log
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
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
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

        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources,binding.mainLayout)

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
            getMyFriendsResponse = { _,_ ->}
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

            callLocatorApiFunctions.getFriendRequests(
                LoginData.getUserPhone(this),
                "0"
            )

//            if (receivedFriendReqList.size == 0)
//                callLocatorApiFunctions.getFriendRequests(
//                    LoginData.getUserPhone(this),
//                    "0"
//                )
//            else {
//                initRv()
//            }

        } else {
            Log.e("Test", "92")

            callLocatorApiFunctions.getFriendRequests(
                LoginData.getUserPhone(this),
                "1"
            )

//            if (sentFriendReqList.size == 0) {
//                Log.e("Test", "95")
//                callLocatorApiFunctions.getFriendRequests(
//                    LoginData.getUserPhone(this),
//                    "1"
//                )
//            } else {
//                initRv()
//            }
        }
    }

    fun initRv() {

        var requestList: ArrayList<GetFriendRequestDataItem> = if (SelectedTab.selectedTab == 0)
            receivedFriendReqList
        else
            sentFriendReqList

        Log.e("Test","Size ${requestList.size}")

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

    private fun onAgreeClicked(getFriendRequestDataItem: GetFriendRequestDataItem) {
        if (getFriendRequestDataItem.requestStatus == "0") {
            val fromNumber = getFriendRequestDataItem.fromNumber!!
            val toNumber = LoginData.getUserPhone(this)

            callLocatorApiFunctions.respondToFriendRequest(fromNumber, toNumber, "1")
        } else {
            val fromNumber = LoginData.getUserPhone(this)
            val toNumber = getFriendRequestDataItem.toNumber!!

            callLocatorApiFunctions.respondToFriendRequest(fromNumber, toNumber, "1")
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

    private fun getFriendRequestsResponse(requestList: List<GetFriendRequestDataItem?>?,isListPresent: Boolean) {
       if(isListPresent) {
           if (SelectedTab.selectedTab == 1)
               sentFriendReqList = requestList as ArrayList<GetFriendRequestDataItem>
           else
               receivedFriendReqList = requestList as ArrayList<GetFriendRequestDataItem>

           initRv()
       }else{
           binding.friendRequestsRv.visibility = View.GONE
           UtilFunctions.showToast(this, "No Items Found")
       }
    }

    object SelectedTab {
        var selectedTab = 0
    }

}