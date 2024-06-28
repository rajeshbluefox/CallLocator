package com.familylocation.mobiletracker.callLocatorModule.apiFunctions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetFriendRequestsResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.GetMyFriendsResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.RespondFriendRequestResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.SendFriendRequestResponse
import com.familylocation.mobiletracker.callLocatorModule.modalClass.UpdateLocationResponse
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

    private var respondFriendRequestResponse = MutableLiveData<RespondFriendRequestResponse>()

    fun acceptFriendRequest(fromNumber: String,toNumber: String,requestStatus: String) {
        viewModelScope.launch {
            respondFriendRequestResponse.postValue(
                callLocatorRepository.acceptFriendRequest(
                    fromNumber, toNumber, requestStatus
                )
            )
        }
    }

    fun resetAcceptFriendRequest() {
        respondFriendRequestResponse = MutableLiveData<RespondFriendRequestResponse>()
    }

    fun getAcceptFriendRequestResponse(): LiveData<RespondFriendRequestResponse> {
        return respondFriendRequestResponse
    }

    private var getMyFriendsResponse = MutableLiveData<GetMyFriendsResponse>()

    fun getMyFriends(fromNumber: String) {
        viewModelScope.launch {
            getMyFriendsResponse.postValue(
                callLocatorRepository.getMyFriends(
                    fromNumber
                )
            )
        }
    }

    fun resetGetMyFriendsResponse() {
        getMyFriendsResponse = MutableLiveData<GetMyFriendsResponse>()
    }

    fun getMyFriendsResponse(): LiveData<GetMyFriendsResponse> {
        return getMyFriendsResponse
    }

    private var updateLocationResponse = MutableLiveData<UpdateLocationResponse>()

    fun updateLocation(phoneNumber: String,lat: String,lng: String) {
        viewModelScope.launch {
            updateLocationResponse.postValue(
                callLocatorRepository.updateLocation(
                    phoneNumber, lat, lng
                )
            )
        }
    }

    fun resetUpdateLocationResponse() {
        updateLocationResponse = MutableLiveData<UpdateLocationResponse>()
    }

    fun getUpdateLocationResponse(): LiveData<UpdateLocationResponse> {
        return updateLocationResponse
    }
}