package realapps.live.callerlocator.callLocatorModule.apiFunctions

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import realapps.live.callerlocator.callLocatorModule.modalClass.GetFriendRequestDataItem

class CallLocatorApiFunctions(
    val context: Context,
    val activity: Activity,
    private val lifeCycleOwner: LifecycleOwner,
    private var callLocatorViewModel: CallLocatorViewModel,
    private val sendFriendRequestObserverCB: (status: Boolean) -> Unit,
    private val getFriendRequestObserverCB:(requestList: List<GetFriendRequestDataItem?>?) -> Unit
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

    fun getFriendRequests(phoneNumber: String,requestType: String)
    {
        callLocatorViewModel.resetGetFriendRequest()
        callLocatorViewModel.getFriendRequests(phoneNumber,requestType)
        getFriendRequestObserver()
    }

    private fun getFriendRequestObserver()
    {
        callLocatorViewModel.getFriendRequestsResponse().observe(lifeCycleOwner) {
            if (it.status != null) {
                if (it.status == 200) {
                    getFriendRequestObserverCB.invoke(it.data)
                }
            }
        }
    }
}
