package com.familylocation.mobiletracker.callLocatorModule.apiFunctions

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetFriendRequestDataItem
import com.familylocation.mobiletracker.callLocatorModule.modalClass.MyFriendsDataItem

class CallLocatorApiFunctions(
    val context: Context,
    val activity: Activity,
    private val lifeCycleOwner: LifecycleOwner,
    private var callLocatorViewModel: CallLocatorViewModel,
    private val sendFriendRequestObserverCB: (status: Boolean) -> Unit,
    private val getFriendRequestObserverCB: (requestList: List<GetFriendRequestDataItem?>?,isListPresent: Boolean) -> Unit,
    private val respondToFriendRequestObserver: (status: Boolean) -> Unit,
    private val getMyFriendsResponse:(data: List<MyFriendsDataItem?>?,allRequests: List<MyFriendsDataItem?>?) -> Unit

    ) {

    fun sendFriendRequest(fromNumber: String, toNumber: String) {
        callLocatorViewModel.resetFriendRequest()
        callLocatorViewModel.sendFriendRequest(fromNumber, toNumber)
        sendFriendRequestObserver()
    }

    private fun sendFriendRequestObserver() {
        callLocatorViewModel.sendFriendRequestResponse().observe(lifeCycleOwner) {
            if (it.data?.status != null) {
                if (it.data.status == 200) {
                    sendFriendRequestObserverCB.invoke(true)
                } else {
                    sendFriendRequestObserverCB.invoke(false)
                }
            }
        }
    }

    fun getFriendRequests(phoneNumber: String, requestType: String) {
        callLocatorViewModel.resetGetFriendRequest()
        callLocatorViewModel.getFriendRequests(phoneNumber, requestType)
        getFriendRequestObserver()
    }

    private fun getFriendRequestObserver() {
        callLocatorViewModel.getFriendRequestsResponse().observe(lifeCycleOwner) {
            if (it.status != null) {
                if (it.status == 200) {
                        getFriendRequestObserverCB.invoke(it.data,true)
                }else{
                    getFriendRequestObserverCB.invoke(emptyList(),false)

                }
            }
        }
    }

    fun respondToFriendRequest(fromNumber: String, toNumber: String, requestStatus: String) {
        callLocatorViewModel.resetAcceptFriendRequest()
        callLocatorViewModel.acceptFriendRequest(fromNumber, toNumber, requestStatus)
        respondToFriendRequestObserver()
    }

    private fun respondToFriendRequestObserver() {
        callLocatorViewModel.getAcceptFriendRequestResponse().observe(lifeCycleOwner) {
            if (it.code != null) {
                if (it.code == 200) {
                    respondToFriendRequestObserver.invoke(true)
                } else {
                    respondToFriendRequestObserver.invoke(false)
                }
            }
        }
    }

    fun getMyFriends(fromNumber: String)
    {
        callLocatorViewModel.resetGetMyFriendsResponse()
        callLocatorViewModel.getMyFriends(fromNumber)
        getMyFriendsResponse()
    }

    private fun getMyFriendsResponse()
    {
        callLocatorViewModel.getMyFriendsResponse().observe(lifeCycleOwner){
            if(it.status==200)
            {
                getMyFriendsResponse.invoke(it.data,it.allFriend_Requests)
            }
        }
    }
}
