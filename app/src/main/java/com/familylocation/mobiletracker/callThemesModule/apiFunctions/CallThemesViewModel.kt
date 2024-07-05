package com.familylocation.mobiletracker.callThemesModule.apiFunctions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.familylocation.mobiletracker.callThemesModule.modalClass.GetThemesResponse
import javax.inject.Inject


@HiltViewModel
class CallThemesViewModel @Inject constructor(
    private val callThemesRepository: CallThemesRepository
) : ViewModel() {

    private var getThemesResponse = MutableLiveData<GetThemesResponse>()

    fun getThemes() {
        viewModelScope.launch {
            getThemesResponse.postValue(
                callThemesRepository.getThemes()
            )
        }
    }

    fun resetGetThemes() {
        getThemesResponse = MutableLiveData<GetThemesResponse>()
    }

    fun getThemesResponse(): LiveData<GetThemesResponse> {
        return getThemesResponse
    }

}