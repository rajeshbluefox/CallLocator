package realapps.live.callerlocator.callLocatorModule.apiFunctions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import realapps.live.callerlocator.callLocatorModule.modalClass.GetFriendRequestsResponse
import realapps.live.callerlocator.callLocatorModule.modalClass.SendFriendRequestResponse
import javax.inject.Inject


@HiltViewModel
class CallLocatorViewModel @Inject constructor(
    private val callLocatorRepository: CallLocatorRepository
) : ViewModel() {

    private var sendFriendRequestResponse = MutableLiveData<SendFriendRequestResponse>()

    fun sendFriendRequest(fromNumber: String, toNumber: String) {
        viewModelScope.launch {
            sendFriendRequestResponse.postValue(
                callLocatorRepository.sendFriendRequest(
                    fromNumber,
                    toNumber
                )
            )
        }
    }

    fun resetFriendRequest() {
        sendFriendRequestResponse = MutableLiveData<SendFriendRequestResponse>()
    }

    fun sendFriendRequestResponse(): LiveData<SendFriendRequestResponse> {
        return sendFriendRequestResponse
    }

    private var getFriendRequestsResponse = MutableLiveData<GetFriendRequestsResponse>()

    fun getFriendRequests(fromNumber: String, requestType: String) {
        viewModelScope.launch {
            getFriendRequestsResponse.postValue(
                callLocatorRepository.getFriendRequests(
                    fromNumber,
                    requestType
                )
            )
        }
    }

    fun resetGetFriendRequest() {
        getFriendRequestsResponse = MutableLiveData<GetFriendRequestsResponse>()
    }

    fun getFriendRequestsResponse(): LiveData<GetFriendRequestsResponse> {
        return getFriendRequestsResponse
    }
}